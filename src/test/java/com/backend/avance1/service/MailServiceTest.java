package com.backend.avance1.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.File;
import java.util.Map;

import static org.mockito.Mockito.*;

class MailServiceTest extends BaseServiceTest {

    @Mock private JavaMailSender mailSender;
    @Mock private MimeMessage mimeMessage;
    @InjectMocks private MailService mailService;

    @Test
    void enviarCorreoHtmlConVariables_DeberiaEnviarCorreo() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailService.enviarCorreoHtmlConVariables(
                "user@test.com",
                "Asunto de prueba",
                "welcome.html",
                Map.of("nombre", "User", "codigo", "123")
        );

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void enviarCorreoHtmlConAdjuntos_DeberiaEnviarConArchivo() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        FileSystemResource[] adjuntos = { new FileSystemResource(new File("src/test/resources/logo.jpg")) };

        mailService.enviarCorreoHtmlConAdjuntos(
                "admin@site.com",
                "Asunto",
                "contact-email.html",
                Map.of("codigo", "456"),
                adjuntos
        );

        verify(mailSender).send(any(MimeMessage.class));
    }
}