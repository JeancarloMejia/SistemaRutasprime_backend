package com.backend.avance1.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void enviarCorreoHtml(String para, String asunto, String templateName, String nombre, String codigo)
            throws MessagingException, IOException {

        String htmlContent = new String(
                new ClassPathResource("templates/" + templateName)
                        .getInputStream()
                        .readAllBytes()
        );

        htmlContent = htmlContent.replace("{{nombre}}", nombre != null ? nombre : "");
        htmlContent = htmlContent.replace("{{codigo}}", codigo != null ? codigo : "");

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(para);
        helper.setSubject(asunto);
        helper.setText(htmlContent, true);

        helper.addInline("logoImage", new ClassPathResource("static/logo.jpg"));

        mailSender.send(mimeMessage);
    }
}
