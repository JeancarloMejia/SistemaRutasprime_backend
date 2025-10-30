package com.backend.avance1.controller;

import com.backend.avance1.dto.ApiResponse;
import com.backend.avance1.dto.ContactDTO;
import com.backend.avance1.dto.ContactDetailDTO;
import com.backend.avance1.dto.ContactReplyDTO;
import com.backend.avance1.entity.ContactMessage;
import com.backend.avance1.service.ContactService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ApiResponse> sendContactMessage(@Valid @RequestBody ContactDTO contactDTO) {
        try {
            contactService.sendContactMessage(contactDTO);
            return ResponseEntity.ok(new ApiResponse(true, "Mensaje enviado correctamente"));
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "Error al enviar el mensaje: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @PostMapping("/reply")
    public ResponseEntity<ApiResponse> replyToUser(@Valid @RequestBody ContactReplyDTO dto) {
        try {
            contactService.replyToUser(dto);
            return ResponseEntity.ok(new ApiResponse(true, "Respuesta enviada correctamente al usuario"));
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "Error al enviar la respuesta: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @GetMapping("/{code}")
    public ResponseEntity<?> getContactByCode(@PathVariable String code) {
        try {
            ContactDetailDTO detail = contactService.getContactByCode(code);
            return ResponseEntity.ok(detail);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<ContactMessage>> getAllContactMessages() {
        List<ContactMessage> messages = contactService.getAllContactMessages();
        return ResponseEntity.ok(messages);
    }
}