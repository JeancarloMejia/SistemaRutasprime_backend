package com.backend.avance1.service;

import com.backend.avance1.dto.ContactDTO;
import com.backend.avance1.dto.ContactReplyDTO;
import com.backend.avance1.dto.ContactDetailDTO;
import com.backend.avance1.entity.ContactMessage;
import com.backend.avance1.entity.ContactReply;
import com.backend.avance1.repository.ContactRepository;
import com.backend.avance1.repository.ContactReplyRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService implements ContactServiceInterface {

    private final JavaMailSender mailSender;
    private final ContactRepository contactRepository;
    private final ContactReplyRepository contactReplyRepository;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.admin}")
    private String adminEmail;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.of("America/Bogota"));

    private String generateMessageCode() {
        Long lastId = contactRepository.findLastId();
        long nextId = (lastId == null) ? 1 : lastId + 1;
        int year = Year.now().getValue();
        return String.format("RP-%d-%04d", year, nextId);
    }

    public void sendContactMessage(ContactDTO dto) throws MessagingException {
        ContactMessage contact = new ContactMessage();
        contact.setMessageCode(generateMessageCode());
        contact.setName(dto.getName());
        contact.setEmail(dto.getEmail());
        contact.setMessage(dto.getMessage());
        contactRepository.save(contact);

        String formattedDate = LocalDateTime.now(ZoneId.of("America/Bogota")).format(DATE_FORMATTER);

        Context context = new Context();
        context.setVariable("name", dto.getName());
        context.setVariable("email", dto.getEmail());
        context.setVariable("message", dto.getMessage());
        context.setVariable("code", contact.getMessageCode());
        context.setVariable("date", formattedDate);

        String htmlContent = templateEngine.process("contact-email", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(adminEmail);
        helper.setReplyTo(dto.getEmail());
        helper.setSubject("Nuevo mensaje de contacto - " + contact.getMessageCode());
        helper.setText(htmlContent, true);

        ClassPathResource logo = new ClassPathResource("static/logo.jpg");
        helper.addInline("logoImage", logo);

        mailSender.send(mimeMessage);
    }

    public void replyToUser(ContactReplyDTO dto) throws MessagingException {
        ContactMessage contact = contactRepository.findTopByEmailOrderByCreatedAtDesc(dto.getEmail());
        if (contact == null) {
            throw new MessagingException("No se encontró un mensaje previo para este correo.");
        }

        long replyCount = contact.getReplies() != null ? contact.getReplies().size() + 1 : 1;
        String replyCode = contact.getMessageCode() + "-R" + replyCount;

        ContactReply reply = new ContactReply();
        reply.setReplyCode(replyCode);
        reply.setReplyMessage(dto.getReplyMessage());
        reply.setContactMessage(contact);
        contactReplyRepository.save(reply);

        String formattedDate = LocalDateTime.now(ZoneId.of("America/Bogota")).format(DATE_FORMATTER);

        Context context = new Context();
        context.setVariable("name", dto.getName());
        context.setVariable("replyMessage", dto.getReplyMessage());
        context.setVariable("code", contact.getMessageCode());
        context.setVariable("date", formattedDate);

        String htmlContent = templateEngine.process("contact-reply", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(dto.getEmail());
        helper.setSubject("Respuesta de Rutas Prime - " + contact.getMessageCode());
        helper.setText(htmlContent, true);

        ClassPathResource logo = new ClassPathResource("static/logo.jpg");
        helper.addInline("logoImage", logo);

        mailSender.send(message);
    }

    public ContactDetailDTO getContactByCode(String code) {
        ContactMessage contact = contactRepository.findByMessageCode(code)
                .orElseThrow(() -> new RuntimeException("No se encontró el mensaje con código: " + code));

        List<ContactDetailDTO.ReplyInfo> replies = contact.getReplies().stream()
                .map(r -> new ContactDetailDTO.ReplyInfo(
                        r.getReplyCode(),
                        r.getReplyMessage(),
                        r.getRepliedAt()
                ))
                .collect(Collectors.toList());

        return new ContactDetailDTO(
                contact.getMessageCode(),
                contact.getName(),
                contact.getEmail(),
                contact.getMessage(),
                contact.getCreatedAt(),
                replies
        );
    }
}