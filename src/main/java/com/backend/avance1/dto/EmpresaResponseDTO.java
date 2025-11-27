package com.backend.avance1.dto;

import com.backend.avance1.entity.Empresa;
import com.backend.avance1.entity.EstadoVerificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaResponseDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String correoCorporativo;
    private String telefono;
    private String nombreEmpresa;
    private String rucEmpresa;
    private String codigoSolicitud;
    private EstadoVerificacion estado;
    private LocalDateTime fechaSolicitud;
    private String observacionAdmin;

    public static EmpresaResponseDTO fromEntity(Empresa empresa) {
        return EmpresaResponseDTO.builder()
                .id(empresa.getId())
                .nombres(empresa.getNombres())
                .apellidos(empresa.getApellidos())
                .dni(empresa.getDni())
                .correoCorporativo(empresa.getCorreoCorporativo())
                .telefono(empresa.getTelefono())
                .nombreEmpresa(empresa.getNombreEmpresa())
                .rucEmpresa(empresa.getRucEmpresa())
                .codigoSolicitud(empresa.getCodigoSolicitud())
                .estado(empresa.getEstado())
                .fechaSolicitud(empresa.getFechaSolicitud())
                .observacionAdmin(empresa.getObservacionAdmin())
                .build();
    }
}