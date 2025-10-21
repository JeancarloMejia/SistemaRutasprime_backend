package com.backend.avance1.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void enviarCorreoHtml(String para, String asunto, String templateName, String nombre, String codigo)
            throws MessagingException, IOException {

        Map<String, Object> variables = new HashMap<>();
        variables.put("nombre", nombre != null ? nombre : "");
        variables.put("codigo", codigo != null ? codigo : "");

        String htmlContent = cargarYReemplazarVariables(templateName, variables);
        MimeMessage mimeMessage = crearMensajeHtml(para, asunto, htmlContent, null);
        mailSender.send(mimeMessage);
    }

    public void enviarCorreoHtmlConVariables(
            String para,
            String asunto,
            String templateName,
            Map<String, Object> variables
    ) throws MessagingException, IOException {

        String htmlContent = cargarYReemplazarVariables(templateName, variables);
        MimeMessage mimeMessage = crearMensajeHtml(para, asunto, htmlContent, null);
        mailSender.send(mimeMessage);
    }

    public void enviarCorreoHtmlConAdjuntos(
            String para,
            String asunto,
            String templateName,
            Map<String, Object> variables,
            FileSystemResource[] adjuntos
    ) throws MessagingException, IOException {

        String htmlContent = cargarYReemplazarVariables(templateName, variables);
        MimeMessage mimeMessage = crearMensajeHtml(para, asunto, htmlContent, adjuntos);
        mailSender.send(mimeMessage);
    }

    private String cargarYReemplazarVariables(String templateName, Map<String, Object> variables) throws IOException {
        String htmlContent = new String(
                new ClassPathResource("templates/" + templateName)
                        .getInputStream()
                        .readAllBytes()
        );

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            htmlContent = htmlContent.replace("{{" + entry.getKey() + "}}", String.valueOf(entry.getValue()));
        }

        return htmlContent;
    }

    private MimeMessage crearMensajeHtml(String para, String asunto, String htmlContent, FileSystemResource[] adjuntos)
            throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(para);
        helper.setSubject(asunto);
        helper.setText(htmlContent, true);
        helper.addInline("logoImage", new ClassPathResource("static/logo.jpg"));

        if (adjuntos != null) {
            for (FileSystemResource archivo : adjuntos) {
                if (archivo.exists()) {
                    helper.addAttachment(archivo.getFilename(), archivo);
                }
            }
        }

        return mimeMessage;
    }
}