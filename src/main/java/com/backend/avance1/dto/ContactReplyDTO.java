package com.backend.avance1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactReplyDTO {

    @NotBlank(message = "El nombre del usuario es obligatorio")
    private String name;

    @NotBlank(message = "El correo del usuario es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String email;

    @NotBlank(message = "El mensaje de respuesta no puede estar vacío")
    private String replyMessage;
}