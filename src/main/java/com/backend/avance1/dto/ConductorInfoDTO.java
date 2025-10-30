package com.backend.avance1.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.Year;

@Data
public class ConductorInfoDTO {

    @NotBlank(message = "La fecha de nacimiento es obligatoria")
    private String fechaNacimiento;

    @NotBlank(message = "El número de licencia de conducir es obligatorio")
    @Pattern(
            regexp = "^[A-Za-z]\\d{8}$",
            message = "Formato inválido. Ejemplo: Q70398332"
    )
    private String numeroLicenciaConducir;

    @NotBlank(message = "La placa es obligatoria")
    @Pattern(
            regexp = "^(?:[A-Z]{3}-?\\d{3}|[A-Z]\\d[A-Z]-?\\d{3})$",
            message = "Formato inválido. Ejemplo: ABC-123 o A1B-234"
    )
    private String placa;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "El color es obligatorio")
    private String color;

    @NotBlank(message = "El año de fabricación es obligatorio")
    @Pattern(
            regexp = "^(19|20)\\d{2}$",
            message = "El año debe tener exactamente 4 dígitos"
    )
    private String anioFabricacion;

    @AssertTrue(message = "El año de fabricación debe estar entre 1995 y el año actual")
    public boolean isAnioFabricacionValido() {
        if (anioFabricacion == null || !anioFabricacion.matches("\\d{4}")) {
            return false;
        }

        int year = Integer.parseInt(anioFabricacion);
        int currentYear = Year.now().getValue();
        return year >= 1995 && year <= currentYear;
    }
}