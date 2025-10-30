package com.backend.avance1.controller;

import com.backend.avance1.dto.ApiResponse;
import com.backend.avance1.dto.ConductorInfoDTO;
import com.backend.avance1.dto.ConductorInfoResponseDTO;
import com.backend.avance1.entity.ConductorInfo;
import com.backend.avance1.entity.EstadoVerificacion;
import com.backend.avance1.entity.ConductorInfoHistorial;
import com.backend.avance1.service.ConductorInfoService;
import com.backend.avance1.repository.ConductorInfoHistorialRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conductor")
@RequiredArgsConstructor
public class ConductorController {

    private final ConductorInfoService conductorInfoService;
    private final ConductorInfoHistorialRepository historialRepository;

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse> apply(
            @AuthenticationPrincipal(expression = "email") String email,
            @Valid @ModelAttribute ConductorInfoDTO dto,
            @RequestParam MultipartFile fotoPersonaLicencia,
            @RequestParam MultipartFile fotoLicencia,
            @RequestParam MultipartFile antecedentesPenales,
            @RequestParam MultipartFile tarjetaPropiedad,
            @RequestParam MultipartFile tarjetaCirculacion,
            @RequestParam MultipartFile soat,
            @RequestParam MultipartFile revisionTecnica
    ) {
        ConductorInfo info = conductorInfoService.registrarSolicitud(
                email,
                dto,
                fotoPersonaLicencia,
                fotoLicencia,
                antecedentesPenales,
                tarjetaPropiedad,
                tarjetaCirculacion,
                soat,
                revisionTecnica
        );
        return ResponseEntity.ok(new ApiResponse(true, "Solicitud enviada correctamente. En espera de verificación.", info));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse> getStatus(@AuthenticationPrincipal(expression = "email") String email) {
        ConductorInfoResponseDTO info = conductorInfoService.obtenerEstadoPorEmail(email);
        if (info == null)
            return ResponseEntity.ok(new ApiResponse(false, "No has realizado una solicitud de conductor aún", null));
        return ResponseEntity.ok(new ApiResponse(true, "Estado de solicitud obtenido correctamente", info));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PutMapping("/verify/{id}")
    public ResponseEntity<ApiResponse> verify(@PathVariable Long id, @RequestBody Map<String, String> body) {
        EstadoVerificacion estado = EstadoVerificacion.valueOf(body.get("estado").toUpperCase());
        String observacion = body.getOrDefault("observacion", "");
        ConductorInfoResponseDTO updated = conductorInfoService.actualizarEstado(id, estado, observacion);
        return ResponseEntity.ok(new ApiResponse(true, "Solicitud actualizada correctamente", updated));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @GetMapping("/historial/{userId}")
    public ResponseEntity<ApiResponse> getHistorial(@PathVariable Long userId) {
        List<ConductorInfoHistorial> historial = historialRepository.findByConductorInfo_User_IdOrderByFechaCambioDesc(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Historial obtenido correctamente", historial));
    }
}