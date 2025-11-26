package com.backend.avance1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserDTO {

    @NotBlank(message = "La dirección es obligatoria")
    @Pattern(
            regexp = "^(?!(.*\\d+[A-Za-z]+))([A-Za-zÁÉÍÓÚÑáéíóúñ]+\\.?\\s)+[0-9]+$",
            message = "La dirección debe tener al menos 2 palabras válidas y terminar con un número (ejemplo: 'Av. Lima 123')"
    )
    private String direccion;

    @NotBlank(message = "El celular es obligatorio")
    @Pattern(
            regexp = "^[0-9]{9}$",
            message = "El celular debe tener exactamente 9 dígitos numéricos"
    )
    private String celular;
}