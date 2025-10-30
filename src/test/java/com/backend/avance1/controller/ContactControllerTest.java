package com.backend.avance1.controller;

import com.backend.avance1.dto.ContactDTO;
import com.backend.avance1.dto.ContactDetailDTO;
import com.backend.avance1.dto.ContactReplyDTO;
import com.backend.avance1.dto.ApiResponse;
import com.backend.avance1.service.ContactService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

class ContactControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContactService contactService;

    @InjectMocks
    private ContactController contactController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(contactController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void sendContactMessage_DeberiaEnviarMensaje() throws Exception {
        ContactDTO dto = new ContactDTO();
        dto.setEmail("user@test.com");
        dto.setMessage("Hola");
        dto.setName("Usuario Test");

        doNothing().when(contactService).sendContactMessage(any(ContactDTO.class));

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Mensaje enviado correctamente"));
    }

    @Test
    void replyToUser_DeberiaResponderAlUsuario() throws Exception {
        ContactReplyDTO dto = new ContactReplyDTO();
        dto.setName("Usuario Test"); // obligatorio
        dto.setEmail("user@test.com");
        dto.setReplyMessage("Gracias por tu mensaje");

        doNothing().when(contactService).replyToUser(any(ContactReplyDTO.class));

        mockMvc.perform(post("/api/contact/reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Respuesta enviada correctamente al usuario"));
    }


    @Test
    void getContactByCode_DeberiaRetornarDetalle() throws Exception {
        ContactDetailDTO detail = new ContactDetailDTO();
        detail.setEmail("user@test.com");
        detail.setMessage("Hola");
        detail.setMessageCode("ABC123");

        when(contactService.getContactByCode("ABC123")).thenReturn(detail);

        mockMvc.perform(get("/api/contact/ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.message").value("Hola"))
                .andExpect(jsonPath("$.messageCode").value("ABC123"));
    }

    @Test
    void getContactByCode_NoExiste_DeberiaRetornar404() throws Exception {
        when(contactService.getContactByCode("XYZ999"))
                .thenThrow(new RuntimeException("Contacto no encontrado"));

        mockMvc.perform(get("/api/contact/XYZ999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Contacto no encontrado"));
    }
}
