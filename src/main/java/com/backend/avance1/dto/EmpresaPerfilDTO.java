package com.backend.avance1.dto;

import com.backend.avance1.entity.Empresa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaPerfilDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String correoCorporativo;
    private String telefono;
    private String nombreEmpresa;
    private String rucEmpresa;
    private boolean activo;
    private LocalDateTime fechaRegistro;

    public static EmpresaPerfilDTO fromEntity(Empresa empresa) {
        return EmpresaPerfilDTO.builder()
                .id(empresa.getId())
                .nombres(empresa.getNombres())
                .apellidos(empresa.getApellidos())
                .dni(empresa.getDni())
                .correoCorporativo(empresa.getCorreoCorporativo())
                .telefono(empresa.getTelefono())
                .nombreEmpresa(empresa.getNombreEmpresa())
                .rucEmpresa(empresa.getRucEmpresa())
                .activo(empresa.isActivo())
                .fechaRegistro(empresa.getFechaRegistro())
                .build();
    }
}