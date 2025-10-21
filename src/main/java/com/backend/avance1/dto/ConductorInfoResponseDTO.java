package com.backend.avance1.dto;

import com.backend.avance1.entity.ConductorInfo;
import com.backend.avance1.entity.EstadoVerificacion;
import lombok.Data;

@Data
public class ConductorInfoResponseDTO {
    private Long id;
    private String codigoSolicitud;
    private String emailUsuario;
    private EstadoVerificacion estado;
    private String observacionAdmin;

    public static ConductorInfoResponseDTO fromEntity(ConductorInfo info) {
        ConductorInfoResponseDTO dto = new ConductorInfoResponseDTO();
        dto.setId(info.getId());
        dto.setCodigoSolicitud(info.getCodigoSolicitud());
        dto.setEstado(info.getEstado());
        dto.setObservacionAdmin(info.getObservacionAdmin());
        if (info.getUser() != null) {
            dto.setEmailUsuario(info.getUser().getEmail());
        }
        return dto;
    }
}