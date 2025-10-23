package com.backend.avance1.service;

import com.backend.avance1.dto.ConductorInfoDTO;
import com.backend.avance1.dto.ConductorInfoResponseDTO;
import com.backend.avance1.entity.ConductorInfo;
import com.backend.avance1.entity.EstadoVerificacion;
import org.springframework.web.multipart.MultipartFile;

public interface ConductorInfoServiceInterface {

    ConductorInfo registrarSolicitud(
            String email,
            ConductorInfoDTO dto,
            MultipartFile fotoPersonaLicencia,
            MultipartFile fotoLicencia,
            MultipartFile antecedentesPenales,
            MultipartFile tarjetaPropiedad,
            MultipartFile tarjetaCirculacion,
            MultipartFile soat,
            MultipartFile revisionTecnica
    );

    ConductorInfoResponseDTO actualizarEstado(Long id, EstadoVerificacion estado, String observacion);
}