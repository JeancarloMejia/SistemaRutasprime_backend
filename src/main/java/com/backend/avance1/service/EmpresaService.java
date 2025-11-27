package com.backend.avance1.service;

import com.backend.avance1.dto.*;
import com.backend.avance1.entity.*;
import com.backend.avance1.repository.EmpresaRepository;
import com.backend.avance1.repository.EmpresaHistorialRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaHistorialRepository historialRepository;
    private final MailService mailService;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${app.conductor.solicitudes.mail}")
    private String correoAdmin;

    public Empresa registrarSolicitudEmpresarial(EmpresaRegistroDTO dto, Map<String, Object> datosSunat) {
        if (empresaRepository.findByCorreoCorporativo(dto.getCorreoCorporativo()).isPresent()) {
            throw new RuntimeException("El correo corporativo ya está registrado.");
        }

        if (userService.buscarPorEmail(dto.getCorreoCorporativo()).isPresent()) {
            throw new RuntimeException("El correo ya está en uso por otro usuario.");
        }

        if (empresaRepository.findByTelefono(dto.getTelefono()).isPresent()) {
            throw new RuntimeException("El teléfono ya está registrado.");
        }

        if (userService.buscarPorCelular(dto.getTelefono()).isPresent()) {
            throw new RuntimeException("El teléfono ya está en uso por otro usuario.");
        }

        if (empresaRepository.findByDni(dto.getDni()).isPresent()) {
            throw new RuntimeException("El DNI ya está registrado.");
        }

        if (userService.buscarPorDniRuc(dto.getDni()).isPresent()) {
            throw new RuntimeException("El DNI ya está en uso por otro usuario.");
        }

        if (empresaRepository.findByRucEmpresa(dto.getRucEmpresa()).isPresent()) {
            throw new RuntimeException("El RUC de la empresa ya está registrado.");
        }

        if (userService.buscarPorDniRuc(dto.getRucEmpresa()).isPresent()) {
            throw new RuntimeException("El RUC ya está en uso por otro usuario.");
        }

        String codigoSolicitud = "EMP-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        ObjectMapper mapper = new ObjectMapper();
        String datosSunatJson;
        try {
            datosSunatJson = mapper.writeValueAsString(datosSunat);
        } catch (Exception e) {
            datosSunatJson = "{}";
        }

        Empresa empresa = Empresa.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .dni(dto.getDni())
                .correoCorporativo(dto.getCorreoCorporativo())
                .telefono(dto.getTelefono())
                .nombreEmpresa(dto.getNombreEmpresa())
                .rucEmpresa(dto.getRucEmpresa())
                .password(passwordEncoder.encode(dto.getPassword()))
                .codigoSolicitud(codigoSolicitud)
                .estado(EstadoVerificacion.PENDIENTE)
                .fechaSolicitud(LocalDateTime.now())
                .fechaRegistro(LocalDateTime.now())
                .activo(false)
                .roles(new HashSet<>())
                .datosSunat(datosSunatJson)
                .build();

        empresa = empresaRepository.save(empresa);
        guardarHistorial(empresa, "Solicitud inicial creada.");
        enviarCorreosSolicitud(empresa);

        return empresa;
    }

    private void guardarHistorial(Empresa empresa, String observacion) {
        EmpresaHistorial historial = EmpresaHistorial.builder()
                .empresa(empresa)
                .estado(empresa.getEstado())
                .codigoSolicitud(empresa.getCodigoSolicitud())
                .fechaCambio(LocalDateTime.now())
                .observacion(observacion)
                .build();

        historialRepository.save(historial);
    }

    private void enviarCorreosSolicitud(Empresa empresa) {
        Map<String, Object> variables = crearVariablesCorreo(empresa);

        try {
            String asuntoAdmin = "[" + empresa.getCodigoSolicitud() + "] - Solicitud de Cuenta Empresarial - RUC " + empresa.getRucEmpresa();
            mailService.enviarCorreoHtmlConVariables(
                    correoAdmin,
                    asuntoAdmin,
                    "empresa-solicitud-admin.html",
                    variables
            );

            String asuntoCliente = "Tu solicitud de cuenta empresarial está en proceso - Código " + empresa.getCodigoSolicitud();
            mailService.enviarCorreoHtmlConVariables(
                    empresa.getCorreoCorporativo(),
                    asuntoCliente,
                    "empresa-solicitud-cliente.html",
                    variables
            );

        } catch (Exception e) {
            throw new RuntimeException("Error al enviar los correos de solicitud empresarial", e);
        }
    }

    private Map<String, Object> crearVariablesCorreo(Empresa empresa) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("codigo", empresa.getCodigoSolicitud());
        variables.put("nombre", empresa.getNombres() + " " + empresa.getApellidos());
        variables.put("dni", empresa.getDni());
        variables.put("email", empresa.getCorreoCorporativo());
        variables.put("telefono", empresa.getTelefono());
        variables.put("nombreEmpresa", empresa.getNombreEmpresa());
        variables.put("ruc", empresa.getRucEmpresa());
        return variables;
    }

    public EmpresaResponseDTO actualizarEstado(Long id, EstadoVerificacion estado, String observacion) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud empresarial no encontrada."));

        empresa.setEstado(estado);
        empresa.setObservacionAdmin(observacion);

        if (estado == EstadoVerificacion.APROBADO) {
            empresa.setActivo(true);
            Set<RoleName> roles = empresa.getRoles();
            roles.add(RoleName.ROLE_CLIENTE);
            empresa.setRoles(roles);
        }

        empresaRepository.save(empresa);
        guardarHistorial(empresa, "Estado actualizado a " + estado.name() + " - " + observacion);

        try {
            enviarCorreoResultado(empresa);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo de resultado", e);
        }

        return EmpresaResponseDTO.fromEntity(empresa);
    }

    private void enviarCorreoResultado(Empresa empresa) throws Exception {
        String fechaVerificacion = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        Map<String, Object> variables = crearVariablesCorreoResultado(empresa, fechaVerificacion);

        String template = empresa.getEstado() == EstadoVerificacion.APROBADO
                ? "empresa-aprobado.html"
                : "empresa-rechazado.html";

        String asunto = empresa.getEstado() == EstadoVerificacion.APROBADO
                ? "Tu cuenta empresarial fue aprobada"
                : "Tu solicitud de cuenta empresarial fue rechazada";

        mailService.enviarCorreoHtmlConVariables(empresa.getCorreoCorporativo(), asunto, template, variables);
    }

    private Map<String, Object> crearVariablesCorreoResultado(Empresa empresa, String fechaVerificacion) {
        Map<String, Object> variables = crearVariablesCorreo(empresa);
        variables.put("estado", empresa.getEstado().name());
        variables.put("observacion", empresa.getObservacionAdmin() != null ? empresa.getObservacionAdmin() : "Sin observaciones");
        variables.put("fechaVerificacion", fechaVerificacion);
        return variables;
    }

    public DatosSunatDTO obtenerDatosSunat(Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        if (empresa.getDatosSunat() == null || empresa.getDatosSunat().isEmpty()) {
            throw new RuntimeException("No hay datos de SUNAT disponibles para esta empresa");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> datosMap = mapper.readValue(
                    empresa.getDatosSunat(),
                    new TypeReference<>() {}
            );

            return DatosSunatDTO.builder()
                    .razonSocial(getStringValue(datosMap, "razon_social"))
                    .tipoDocumento(getStringValue(datosMap, "tipo_documento"))
                    .numeroDocumento(getStringValue(datosMap, "numero_documento"))
                    .estado(getStringValue(datosMap, "estado"))
                    .condicion(getStringValue(datosMap, "condicion"))
                    .direccion(getStringValue(datosMap, "direccion"))
                    .ubigeo(getStringValue(datosMap, "ubigeo"))
                    .viaTipo(getStringValue(datosMap, "via_tipo"))
                    .viaNombre(getStringValue(datosMap, "via_nombre"))
                    .zonaCodigo(getStringValue(datosMap, "zona_codigo"))
                    .zonaTipo(getStringValue(datosMap, "zona_tipo"))
                    .numero(getStringValue(datosMap, "numero"))
                    .interior(getStringValue(datosMap, "interior"))
                    .lote(getStringValue(datosMap, "lote"))
                    .dpto(getStringValue(datosMap, "dpto"))
                    .manzana(getStringValue(datosMap, "manzana"))
                    .kilometro(getStringValue(datosMap, "kilometro"))
                    .distrito(getStringValue(datosMap, "distrito"))
                    .provincia(getStringValue(datosMap, "provincia"))
                    .departamento(getStringValue(datosMap, "departamento"))
                    .esAgenteRetencion(getBooleanValue(datosMap, "es_agente_retencion"))
                    .esBuenContribuyente(getBooleanValue(datosMap, "es_buen_contribuyente"))
                    .localesAnexos(getStringValue(datosMap, "locales_anexos"))
                    .tipo(getStringValue(datosMap, "tipo"))
                    .actividadEconomica(getStringValue(datosMap, "actividad_economica"))
                    .numeroTrabajadores(getStringValue(datosMap, "numero_trabajadores"))
                    .tipoFacturacion(getStringValue(datosMap, "tipo_facturacion"))
                    .tipoContabilidad(getStringValue(datosMap, "tipo_contabilidad"))
                    .comercioExterior(getStringValue(datosMap, "comercio_exterior"))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar datos de SUNAT: " + e.getMessage());
        }
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    private Boolean getBooleanValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return false;
    }

    public List<Empresa> listarTodasSolicitudes() {
        return empresaRepository.findAllByOrderByFechaSolicitudDesc();
    }

    public Optional<Empresa> obtenerPorId(Long id) {
        return empresaRepository.findById(id);
    }

    public Optional<Empresa> buscarPorEmail(String email) {
        return empresaRepository.findByCorreoCorporativo(email);
    }

    public List<EmpresaHistorial> obtenerHistorial(Long empresaId) {
        return historialRepository.findByEmpresa_IdOrderByFechaCambioDesc(empresaId);
    }

    public void actualizarPassword(Empresa empresa, String nuevaPassword) {
        empresa.setPassword(passwordEncoder.encode(nuevaPassword));
        empresaRepository.save(empresa);
    }

    public EmpresaPerfilDTO obtenerPerfil(String email) {
        Empresa empresa = empresaRepository.findByCorreoCorporativo(email)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        return EmpresaPerfilDTO.fromEntity(empresa);
    }

    public long contarTotalDeEmpresas() {
        return empresaRepository.count();
    }

    public EmpresaResponseDTO actualizarEmpresa(Long id, EmpresaUpdateDTO dto) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        if (!empresa.getCorreoCorporativo().equals(dto.getCorreoCorporativo())) {
            if (empresaRepository.findByCorreoCorporativo(dto.getCorreoCorporativo()).isPresent()) {
                throw new RuntimeException("El correo corporativo ya está registrado por otra empresa");
            }
            if (userService.buscarPorEmail(dto.getCorreoCorporativo()).isPresent()) {
                throw new RuntimeException("El correo ya está en uso por otro usuario");
            }
        }

        if (!empresa.getTelefono().equals(dto.getTelefono())) {
            if (empresaRepository.findByTelefono(dto.getTelefono()).isPresent()) {
                throw new RuntimeException("El teléfono ya está registrado por otra empresa");
            }
            if (userService.buscarPorCelular(dto.getTelefono()).isPresent()) {
                throw new RuntimeException("El teléfono ya está en uso por otro usuario");
            }
        }

        empresa.setNombres(dto.getNombres());
        empresa.setApellidos(dto.getApellidos());
        empresa.setCorreoCorporativo(dto.getCorreoCorporativo());
        empresa.setTelefono(dto.getTelefono());
        empresa.setNombreEmpresa(dto.getNombreEmpresa());

        empresaRepository.save(empresa);
        guardarHistorial(empresa, "Datos actualizados por administrador");

        return EmpresaResponseDTO.fromEntity(empresa);
    }

    public void eliminarEmpresa(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        historialRepository.deleteAll(historialRepository.findByEmpresa_IdOrderByFechaCambioDesc(id));
        empresaRepository.delete(empresa);
    }

    public List<Empresa> listarEmpresasAprobadas() {
        return empresaRepository.findAllByEstadoOrderByFechaSolicitudDesc(EstadoVerificacion.APROBADO);
    }
}