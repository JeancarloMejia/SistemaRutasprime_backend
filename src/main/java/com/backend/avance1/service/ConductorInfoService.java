package com.backend.avance1.service;

import com.backend.avance1.dto.ConductorInfoDTO;
import com.backend.avance1.dto.ConductorInfoResponseDTO;
import com.backend.avance1.entity.*;
import com.backend.avance1.repository.ConductorInfoRepository;
import com.backend.avance1.repository.ConductorInfoHistorialRepository;
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
    private final ConductorInfoHistorialRepository historialRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final MailService mailService;

    @Value("${app.conductor.solicitudes.mail}")
    private String correoDestino;

    @Value("${app.base.url}")
    private String baseUrl;

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

        String dni = user.getDniRuc();
        String codigoSolicitud = "COND-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        String rutaFisicaFotoPersonaLicencia = fileStorageService.guardarArchivo(dni, fotoPersonaLicencia, "foto_persona_licencia");
        String rutaFisicaFotoLicencia = fileStorageService.guardarArchivo(dni, fotoLicencia, "foto_licencia");
        String rutaFisicaAntecedentes = fileStorageService.guardarArchivo(dni, antecedentesPenales, "antecedentes_penales");
        String rutaFisicaTarjetaProp = fileStorageService.guardarArchivo(dni, tarjetaPropiedad, "tarjeta_propiedad");
        String rutaFisicaTarjetaCirc = fileStorageService.guardarArchivo(dni, tarjetaCirculacion, "tarjeta_circulacion");
        String rutaFisicaSoat = fileStorageService.guardarArchivo(dni, soat, "soat");
        String rutaFisicaRevision = fileStorageService.guardarArchivo(dni, revisionTecnica, "revision_tecnica");

        String baseArchivoUrl = baseUrl + "/api/archivos/" + dni + "/";

        String urlFotoPersonaLicencia = baseArchivoUrl + "foto_persona_licencia" + obtenerExtension(fotoPersonaLicencia.getOriginalFilename());
        String urlFotoLicencia = baseArchivoUrl + "foto_licencia" + obtenerExtension(fotoLicencia.getOriginalFilename());
        String urlAntecedentes = baseArchivoUrl + "antecedentes_penales" + obtenerExtension(antecedentesPenales.getOriginalFilename());
        String urlTarjetaProp = baseArchivoUrl + "tarjeta_propiedad" + obtenerExtension(tarjetaPropiedad.getOriginalFilename());
        String urlTarjetaCirc = baseArchivoUrl + "tarjeta_circulacion" + obtenerExtension(tarjetaCirculacion.getOriginalFilename());
        String urlSoat = baseArchivoUrl + "soat" + obtenerExtension(soat.getOriginalFilename());
        String urlRevision = baseArchivoUrl + "revision_tecnica" + obtenerExtension(revisionTecnica.getOriginalFilename());

        Optional<ConductorInfo> solicitudExistenteOpt = conductorInfoRepository.findByUser(user);
        ConductorInfo info;

        if (solicitudExistenteOpt.isPresent()) {
            ConductorInfo existente = solicitudExistenteOpt.get();

            if (existente.getEstado() == EstadoVerificacion.PENDIENTE) {
                throw new RuntimeException("Ya tienes una solicitud pendiente de verificación.");
            }

            if (existente.getEstado() == EstadoVerificacion.APROBADO) {
                throw new RuntimeException("Tu solicitud ya fue aprobada. No puedes enviar otra.");
            }

            existente.setCodigoSolicitud(codigoSolicitud);
            existente.setFechaSolicitud(LocalDateTime.now());
            existente.setFechaNacimiento(dto.getFechaNacimiento());
            existente.setNumeroLicenciaConducir(dto.getNumeroLicenciaConducir().toUpperCase());
            existente.setPlaca(dto.getPlaca().toUpperCase());
            existente.setMarca(dto.getMarca());
            existente.setColor(dto.getColor());
            existente.setAnioFabricacion(dto.getAnioFabricacion());
            existente.setFotoPersonaLicencia(urlFotoPersonaLicencia);
            existente.setFotoLicencia(urlFotoLicencia);
            existente.setAntecedentesPenales(urlAntecedentes);
            existente.setTarjetaPropiedad(urlTarjetaProp);
            existente.setTarjetaCirculacion(urlTarjetaCirc);
            existente.setSoat(urlSoat);
            existente.setRevisionTecnica(urlRevision);
            existente.setEstado(EstadoVerificacion.PENDIENTE);
            existente.setObservacionAdmin(null);

            info = conductorInfoRepository.save(existente);

            guardarHistorial(info, generarTextoIntento(user));

        } else {
            info = ConductorInfo.builder()
                    .user(user)
                    .codigoSolicitud(codigoSolicitud)
                    .fechaSolicitud(LocalDateTime.now())
                    .fechaNacimiento(dto.getFechaNacimiento())
                    .numeroLicenciaConducir(dto.getNumeroLicenciaConducir().toUpperCase())
                    .placa(dto.getPlaca().toUpperCase())
                    .marca(dto.getMarca())
                    .color(dto.getColor())
                    .anioFabricacion(dto.getAnioFabricacion())
                    .fotoPersonaLicencia(urlFotoPersonaLicencia)
                    .fotoLicencia(urlFotoLicencia)
                    .antecedentesPenales(urlAntecedentes)
                    .tarjetaPropiedad(urlTarjetaProp)
                    .tarjetaCirculacion(urlTarjetaCirc)
                    .soat(urlSoat)
                    .revisionTecnica(urlRevision)
                    .estado(EstadoVerificacion.PENDIENTE)
                    .build();

            info = conductorInfoRepository.save(info);
            guardarHistorial(info, "Solicitud inicial creada.");
        }

        Map<String, Object> variables = crearVariablesCorreo(user, dto, dni, codigoSolicitud);
        FileSystemResource[] adjuntos = {
                new FileSystemResource(new File(rutaFisicaFotoPersonaLicencia)),
                new FileSystemResource(new File(rutaFisicaFotoLicencia)),
                new FileSystemResource(new File(rutaFisicaAntecedentes)),
                new FileSystemResource(new File(rutaFisicaTarjetaProp)),
                new FileSystemResource(new File(rutaFisicaTarjetaCirc)),
                new FileSystemResource(new File(rutaFisicaSoat)),
                new FileSystemResource(new File(rutaFisicaRevision))
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
        guardarHistorial(info, "Estado actualizado a " + estado.name() + " - " + observacion);

        try {
            enviarCorreoResultado(info);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo de resultado", e);
        }

        return ConductorInfoResponseDTO.fromEntity(info);
    }

    private void guardarHistorial(ConductorInfo info, String observacion) {
        ConductorInfoHistorial historial = ConductorInfoHistorial.builder()
                .conductorInfo(info)
                .user(info.getUser())
                .estado(info.getEstado())
                .codigoSolicitud(info.getCodigoSolicitud())
                .fechaCambio(LocalDateTime.now())
                .observacion(observacion)
                .build();

        historialRepository.save(historial);
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

        mailService.enviarCorreoHtmlConVariables(user.getEmail(), asunto, template, variables);
    }

    private String generarTextoIntento(User user) {
        List<ConductorInfoHistorial> historialPrevio =
                historialRepository.findByConductorInfo_User_IdOrderByFechaCambioDesc(user.getId());

        long cantidadIntentos = historialPrevio.stream()
                .filter(h -> Optional.ofNullable(h.getObservacion()).orElse("")
                        .toLowerCase().contains("intento"))
                .count() + 1;

        return switch ((int) cantidadIntentos) {
            case 1 -> "Segundo intento de solicitud.";
            case 2 -> "Tercer intento de solicitud.";
            case 3 -> "Cuarto intento de solicitud.";
            default -> cantidadIntentos + "° intento de solicitud.";
        };
    }

    public List<ConductorInfo> listarTodasSolicitudesEntity() {
        return conductorInfoRepository.findAllByOrderByFechaSolicitudDesc();
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
    }
}