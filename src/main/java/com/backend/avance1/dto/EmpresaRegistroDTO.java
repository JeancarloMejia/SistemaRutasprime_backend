package com.backend.avance1.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EmpresaRegistroDTO {

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

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(
            regexp = "^\\d{8}$",
            message = "El DNI debe tener exactamente 8 dígitos"
    )
    private String dni;

    @NotBlank(message = "El correo corporativo es obligatorio")
    @Email(message = "Formato de correo inválido")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|org|net|edu|pe|es|gov)$",
            message = "El correo debe tener un dominio válido"
    )
    private String correoCorporativo;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
            regexp = "^[0-9]{9}$",
            message = "El teléfono debe tener exactamente 9 dígitos"
    )
    private String telefono;

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(min = 3, max = 255, message = "El nombre de la empresa debe tener entre 3 y 255 caracteres")
    private String nombreEmpresa;

    @NotBlank(message = "El RUC de la empresa es obligatorio")
    @Pattern(
            regexp = "^\\d{11}$",
            message = "El RUC debe tener exactamente 11 dígitos"
    )
    private String rucEmpresa;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un caracter especial"
    )
    private String password;
}