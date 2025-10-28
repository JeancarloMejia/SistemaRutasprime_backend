package com.backend.avance1.service;

import com.backend.avance1.dto.ConductorInfoDTO;
import com.backend.avance1.dto.ConductorInfoResponseDTO;
import com.backend.avance1.entity.*;
import com.backend.avance1.repository.ConductorInfoRepository;
import com.backend.avance1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ConductorInfoService implements ConductorInfoServiceInterface {

    private final ConductorInfoRepository conductorInfoRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final MailService mailService;

    @Value("${app.conductor.solicitudes.mail}")
    private String correoDestino;

    public ConductorInfo registrarSolicitud(
            String email,
            ConductorInfoDTO dto,
            MultipartFile fotoPersonaLicencia,
            MultipartFile fotoLicencia,
            MultipartFile antecedentesPenales,
            MultipartFile tarjetaPropiedad,
            MultipartFile tarjetaCirculacion,
            MultipartFile soat,
            MultipartFile revisionTecnica
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!user.isActivo()) {
            throw new RuntimeException("El usuario debe tener la cuenta activa para aplicar como conductor.");
        }

        if (!user.getRoles().contains(RoleName.ROLE_CLIENTE)) {
            throw new RuntimeException("Solo los usuarios con rol CLIENTE pueden aplicar para ser conductor.");
        }

        if (conductorInfoRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Ya existe una solicitud pendiente o aprobada para este usuario.");
        }

        String dni = user.getDniRuc();
        String codigoSolicitud = "COND-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        String rutaFotoPersonaLicencia = fileStorageService.guardarArchivo(dni, fotoPersonaLicencia, "foto_persona_licencia");
        String rutaFotoLicencia = fileStorageService.guardarArchivo(dni, fotoLicencia, "foto_licencia");
        String rutaAntecedentes = fileStorageService.guardarArchivo(dni, antecedentesPenales, "antecedentes_penales");
        String rutaTarjetaProp = fileStorageService.guardarArchivo(dni, tarjetaPropiedad, "tarjeta_propiedad");
        String rutaTarjetaCirc = fileStorageService.guardarArchivo(dni, tarjetaCirculacion, "tarjeta_circulacion");
        String rutaSoat = fileStorageService.guardarArchivo(dni, soat, "soat");
        String rutaRevision = fileStorageService.guardarArchivo(dni, revisionTecnica, "revision_tecnica");

        ConductorInfo info = ConductorInfo.builder()
                .user(user)
                .codigoSolicitud(codigoSolicitud)
                .fechaSolicitud(LocalDateTime.now())
                .fechaNacimiento(dto.getFechaNacimiento())
                .numeroLicenciaConducir(dto.getNumeroLicenciaConducir().toUpperCase())
                .placa(dto.getPlaca().toUpperCase())
                .marca(dto.getMarca())
                .color(dto.getColor())
                .anioFabricacion(dto.getAnioFabricacion())
                .fotoPersonaLicencia(rutaFotoPersonaLicencia)
                .fotoLicencia(rutaFotoLicencia)
                .antecedentesPenales(rutaAntecedentes)
                .tarjetaPropiedad(rutaTarjetaProp)
                .tarjetaCirculacion(rutaTarjetaCirc)
                .soat(rutaSoat)
                .revisionTecnica(rutaRevision)
                .estado(EstadoVerificacion.PENDIENTE)
                .build();

        info = conductorInfoRepository.save(info);

        Map<String, Object> variables = crearVariablesCorreo(user, dto, dni, codigoSolicitud);

        FileSystemResource[] adjuntos = {
                new FileSystemResource(new File(rutaFotoPersonaLicencia)),
                new FileSystemResource(new File(rutaFotoLicencia)),
                new FileSystemResource(new File(rutaAntecedentes)),
                new FileSystemResource(new File(rutaTarjetaProp)),
                new FileSystemResource(new File(rutaTarjetaCirc)),
                new FileSystemResource(new File(rutaSoat)),
                new FileSystemResource(new File(rutaRevision))
        };

        try {
            String asuntoAdmin = "[" + codigoSolicitud + "] - Solicitud de Conductor - DNI " + dni;
            mailService.enviarCorreoHtmlConAdjuntos(
                    correoDestino,
                    asuntoAdmin,
                    "conductor-apply.html",
                    variables,
                    adjuntos
            );

            String asuntoCliente = "Tu solicitud de conductor está en proceso - Código " + codigoSolicitud;
            mailService.enviarCorreoHtmlConAdjuntos(
                    user.getEmail(),
                    asuntoCliente,
                    "conductor-confirmacion.html",
                    variables,
                    null
            );

        } catch (Exception e) {
            throw new RuntimeException("Error al enviar los correos de solicitud", e);
        }

        return info;
    }

    public ConductorInfoResponseDTO actualizarEstado(Long id, EstadoVerificacion estado, String observacion) {
        ConductorInfo info = conductorInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada."));

        info.setEstado(estado);
        info.setObservacionAdmin(observacion);

        User user = info.getUser();

        if (estado == EstadoVerificacion.APROBADO) {
            userService.promoverAConductor(user.getEmail());
        } else if (estado == EstadoVerificacion.RECHAZADO) {
            fileStorageService.eliminarCarpetaUsuario(user.getDniRuc());
        }

        conductorInfoRepository.save(info);

        try {
            enviarCorreoResultado(info);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo de resultado", e);
        }

        return ConductorInfoResponseDTO.fromEntity(info);
    }

    public ConductorInfoResponseDTO obtenerEstadoPorEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        Optional<ConductorInfo> infoOpt = conductorInfoRepository.findByUser(user);

        return infoOpt.map(ConductorInfoResponseDTO::fromEntity).orElse(null);
    }

    private Map<String, Object> crearVariablesCorreo(User user, ConductorInfoDTO dto, String dni, String codigo) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("codigo", codigo);
        variables.put("nombre", user.getNombres() + " " + user.getApellidos());
        variables.put("email", user.getEmail());
        variables.put("celular", user.getCelular());
        variables.put("direccion", user.getDireccion());
        variables.put("dni", dni);
        variables.put("fechaNacimiento", dto.getFechaNacimiento());
        variables.put("licencia", dto.getNumeroLicenciaConducir());
        variables.put("placa", dto.getPlaca());
        variables.put("marca", dto.getMarca());
        variables.put("color", dto.getColor());
        variables.put("anio", dto.getAnioFabricacion());
        return variables;
    }

    private void enviarCorreoResultado(ConductorInfo info) throws Exception {
        User user = info.getUser();

        String fechaVerificacion = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        Map<String, Object> variables = new HashMap<>();
        variables.put("nombre", user.getNombres() + " " + user.getApellidos());
        variables.put("codigo", info.getCodigoSolicitud());
        variables.put("estado", info.getEstado().name());
        variables.put("observacion", info.getObservacionAdmin() != null ? info.getObservacionAdmin() : "Sin observaciones");
        variables.put("fechaVerificacion", fechaVerificacion);

        String template = info.getEstado() == EstadoVerificacion.APROBADO
                ? "conductor-aprobado.html"
                : "conductor-rechazado.html";

        String asunto = info.getEstado() == EstadoVerificacion.APROBADO
                ? "Tu solicitud de conductor fue aprobada"
                : "Tu solicitud de conductor fue rechazada";

        mailService.enviarCorreoHtmlConVariables(
                user.getEmail(),
                asunto,
                template,
                variables
        );
    }
}