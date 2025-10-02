package com.backend.avance1.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDTO {

    @NotBlank(message = "Los nombres son obligatorios")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚÑáéíóúñ ]{3,}$",
            message = "Nombres solo letras y mínimo 3 caracteres"
    )
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚÑáéíóúñ ]{3,}$",
            message = "Apellidos solo letras y mínimo 3 caracteres"
    )
    private String apellidos;

    @NotBlank(message = "El celular es obligatorio")
    @Pattern(
            regexp = "^[0-9]{9}$",
            message = "El celular debe tener exactamente 9 dígitos numéricos"
    )
    private String celular;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de correo inválido")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|org|net|edu|pe|es|gov)$",
            message = "El correo debe tener un dominio válido (.com, .org, .net, .edu, .pe, .es, .gov)"
    )
    private String email;

    @NotBlank(message = "La dirección es obligatoria")
    @Pattern(
            regexp = "^(?!(.*\\d+[A-Za-z]+))([A-Za-zÁÉÍÓÚÑáéíóúñ]+\\.?\\s)+[0-9]+$",
            message = "La dirección debe contener mínimo 2 palabras válidas (ej. 'Av. Lima 123', 'Calle Los Olivos 456')"
    )
    private String direccion;

    @NotBlank(message = "El DNI/RUC es obligatorio")
    @Pattern(
            regexp = "^(\\d{8}|\\d{11})$",
            message = "El DNI debe tener 8 dígitos y el RUC 11, solo números"
    )
    private String dniRuc;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un caracter especial"
    )
    private String password;
}