package com.backend.avance1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserDTO {

    @NotBlank(message = "Los nombres son obligatorios")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚÑáéíóúñ ]{3,}$",
            message = "Los nombres solo pueden contener letras y deben tener mínimo 3 caracteres"
    )
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚÑáéíóúñ ]{3,}$",
            message = "Los apellidos solo pueden contener letras y deben tener mínimo 3 caracteres"
    )
    private String apellidos;

    @NotBlank(message = "La dirección es obligatoria")
    @Pattern(
            regexp = "^(?!(.*\\d+[A-Za-z]+))([A-Za-zÁÉÍÓÚÑáéíóúñ]+\\.?\\s)+[0-9]+$",
            message = "La dirección debe tener al menos 2 palabras válidas y terminar con un número (ejemplo: 'Av. Lima 123')"
    )
    private String direccion;
}