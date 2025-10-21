package com.backend.avance1.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ConductorInfoDTO {

    @NotBlank(message = "La fecha de nacimiento es obligatoria")
    private String fechaNacimiento;

    @NotBlank(message = "El número de licencia de conducir es obligatorio")
    @Pattern(regexp = "^[A-Z0-9-]{6,15}$", message = "Número de licencia inválido (6–15 caracteres alfanuméricos)")
    private String numeroLicenciaConducir;

    @NotBlank(message = "La placa es obligatoria")
    @Pattern(regexp = "^[A-Z0-9-]{5,10}$", message = "Placa inválida (solo letras, números y guiones)")
    private String placa;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "El color es obligatorio")
    private String color;

    @NotBlank(message = "El año de fabricación es obligatorio")
    @Pattern(regexp = "^(19|20)\\d{2}$", message = "Año de fabricación inválido")
    private String anioFabricacion;
}