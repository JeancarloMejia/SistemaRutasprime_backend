package com.backend.avance1.controller;

import com.backend.avance1.dto.*;
import com.backend.avance1.entity.Empresa;
import com.backend.avance1.entity.EmpresaHistorial;
import com.backend.avance1.entity.EstadoVerificacion;
import com.backend.avance1.security.JwtUtil;
import com.backend.avance1.service.DniValidatorService;
import com.backend.avance1.service.EmpresaService;
import com.backend.avance1.service.RucValidatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/empresa")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;
    private final DniValidatorService dniValidatorService;
    private final RucValidatorService rucValidatorService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registrarSolicitud(@Valid @RequestBody EmpresaRegistroDTO dto) {
        try {
            boolean dniCoincide = dniValidatorService.validarDatosReniecEmpresa(dto);
            if (!dniCoincide) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse(false, "Los datos del representante no coinciden con RENIEC")
                );
            }

            RucValidatorService.RucValidationResult rucResult = rucValidatorService.validarDatosSunat(dto);
            if (!rucResult.coincide()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse(false, "La razón social no coincide con SUNAT")
                );
            }

            Empresa empresa = empresaService.registrarSolicitudEmpresarial(dto, rucResult.datosCompletos());
            return ResponseEntity.ok(new ApiResponse(true,
                    "Solicitud empresarial enviada correctamente. En espera de verificación por un administrador.",
                    EmpresaResponseDTO.fromEntity(empresa)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginEmpresa(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        Optional<Empresa> empresaOpt = empresaService.buscarPorEmail(email);
        if (empresaOpt.isPresent()) {
            Empresa empresa = empresaOpt.get();
            if (empresa.isActivo() && passwordEncoder.matches(password, empresa.getPassword())) {
                String token = jwtUtil.generateTokenEmpresa(empresa);
                return ResponseEntity.ok(
                        new ApiResponse(true, "Login exitoso", Map.of(
                                "token", token,
                                "tipo", "empresa",
                                "nombreEmpresa", empresa.getNombreEmpresa(),
                                "ruc", empresa.getRucEmpresa()
                        ))
                );
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, "Credenciales inválidas o cuenta no aprobada"));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> obtenerPerfil(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);

            EmpresaPerfilDTO perfil = empresaService.obtenerPerfil(email);
            return ResponseEntity.ok(new ApiResponse(true, "Perfil obtenido correctamente", perfil));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Token inválido o expirado"));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PutMapping("/verify/{id}")
    public ResponseEntity<ApiResponse> verificarSolicitud(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        EstadoVerificacion estado = EstadoVerificacion.valueOf(body.get("estado").toUpperCase());
        String observacion = body.getOrDefault("observacion", "");

        EmpresaResponseDTO updated = empresaService.actualizarEstado(id, estado, observacion);
        return ResponseEntity.ok(new ApiResponse(true, "Solicitud empresarial actualizada correctamente", updated));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> listarSolicitudes() {
        List<Empresa> solicitudes = empresaService.listarTodasSolicitudes();
        return ResponseEntity.ok(new ApiResponse(true, "Solicitudes empresariales listadas correctamente", solicitudes));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> obtenerSolicitudPorId(@PathVariable Long id) {
        return empresaService.obtenerPorId(id)
                .map(empresa -> ResponseEntity.ok(
                        new ApiResponse(true, "Solicitud encontrada", EmpresaResponseDTO.fromEntity(empresa))
                ))
                .orElseGet(() -> ResponseEntity
                        .badRequest()
                        .body(new ApiResponse(false, "Solicitud no encontrada")));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @GetMapping("/{id}/datos-sunat")
    public ResponseEntity<ApiResponse> obtenerDatosSunat(@PathVariable Long id) {
        try {
            DatosSunatDTO datosSunat = empresaService.obtenerDatosSunat(id);
            return ResponseEntity.ok(new ApiResponse(true, "Datos de SUNAT obtenidos correctamente", datosSunat));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @GetMapping("/historial/{empresaId}")
    public ResponseEntity<ApiResponse> obtenerHistorial(@PathVariable Long empresaId) {
        List<EmpresaHistorial> historial = empresaService.obtenerHistorial(empresaId);
        return ResponseEntity.ok(new ApiResponse(true, "Historial obtenido correctamente", historial));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse> contarEmpresas() {
        long totalEmpresas = empresaService.contarTotalDeEmpresas();
        return ResponseEntity.ok(new ApiResponse(true, "Total de empresas registradas", totalEmpresas));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> actualizarEmpresa(
            @PathVariable Long id,
            @Valid @RequestBody EmpresaUpdateDTO dto) {
        try {
            EmpresaResponseDTO updated = empresaService.actualizarEmpresa(id, dto);
            return ResponseEntity.ok(new ApiResponse(true, "Empresa actualizada correctamente", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> eliminarEmpresa(@PathVariable Long id) {
        try {
            empresaService.eliminarEmpresa(id);
            return ResponseEntity.ok(new ApiResponse(true, "Empresa eliminada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @GetMapping("/list/approved")
    public ResponseEntity<ApiResponse> listarEmpresasAprobadas() {
        try {
            List<Empresa> empresasAprobadas = empresaService.listarEmpresasAprobadas();
            if (empresasAprobadas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "No se encontraron empresas aprobadas"));
            }
            return ResponseEntity.ok(new ApiResponse(true, "Empresas aprobadas listadas correctamente", empresasAprobadas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error al listar las empresas aprobadas: " + e.getMessage()));
        }
    }
}