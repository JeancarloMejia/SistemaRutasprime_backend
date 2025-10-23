package com.backend.avance1.service;

import jakarta.mail.MessagingException;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.util.Map;

public interface MailServiceInterface {

    void enviarCorreoHtml(String para, String asunto, String templateName, String nombre, String codigo)
            throws MessagingException, IOException;

    void enviarCorreoHtmlConVariables(String para, String asunto, String templateName, Map<String, Object> variables)
            throws MessagingException, IOException;

    void enviarCorreoHtmlConAdjuntos(String para, String asunto, String templateName, Map<String, Object> variables, FileSystemResource[] adjuntos)
            throws MessagingException, IOException;
}