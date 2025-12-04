package com.backend.avance1.service;

import com.backend.avance1.entity.ConductorInfo;
import com.backend.avance1.entity.EstadoVerificacion;
import com.backend.avance1.entity.EstadoViaje;
import com.backend.avance1.entity.User;
import com.backend.avance1.entity.Viaje;
import com.backend.avance1.repository.ConductorInfoRepository;
import com.backend.avance1.repository.UserRepository;
import com.backend.avance1.repository.ViajeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViajeService {

    private final ViajeRepository viajeRepository;
    private final UserRepository userRepository;
    private final ConductorInfoRepository conductorInfoRepository;
    private final PdfService pdfService;
    private final MailService mailService;

    @Transactional
    public Viaje crearViaje(Viaje viaje) {
        log.info("Creando viaje para {} {}", viaje.getNombre(), viaje.getApellido());
        return viajeRepository.save(viaje);
    }

    public Optional<Viaje> obtenerViajeActivo(String email) {
        log.info("Buscando viaje activo para email: {}", email);
        return viajeRepository.findViajeActivoByEmail(email);
    }

    public Optional<Viaje> obtenerViajePorId(Long id) {
        return viajeRepository.findById(id);
    }

    public List<Viaje> obtenerHistorial(String email) {
        log.info("Obteniendo historial de viajes para: {}", email);
        return viajeRepository.findHistorialByEmail(email);
    }

    @Transactional
    public Viaje actualizarEstado(Long viajeId, EstadoViaje nuevoEstado) {
        log.info("Actualizando estado del viaje {} a {}", viajeId, nuevoEstado);
        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado con id: " + viajeId));
        viaje.setEstado(nuevoEstado);
        return viajeRepository.save(viaje);
    }

    @Transactional
    public Viaje vincularPago(Long viajeId, String chargeId) {
        log.info("Vinculando pago {} al viaje {}", chargeId, viajeId);
        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado"));
        viaje.setChargeId(chargeId);
        return viajeRepository.save(viaje);
    }

    public List<Viaje> obtenerViajesDisponibles() {
        log.info("Buscando viajes disponibles para conductores");
        return viajeRepository.findViajesPendientes();
    }

    @Transactional
    public Viaje asignarViajeAConductor(Long viajeId, Long conductorId) {
        log.info("Asignando viaje {} al conductor {}", viajeId, conductorId);

        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado con id: " + viajeId));

        if (viaje.getEstado() != EstadoViaje.BUSCANDO_CONDUCTOR) {
            throw new RuntimeException("El viaje ya no está disponible");
        }

        User conductor = userRepository.findById(conductorId)
                .orElseThrow(() -> new RuntimeException("Usuario conductor no encontrado con id: " + conductorId));

        ConductorInfo conductorInfo = conductorInfoRepository.findByUserId(conductorId)
                .orElseThrow(() -> new RuntimeException("Información del conductor no encontrada para user id: " + conductorId));

        if (conductorInfo.getEstado() != EstadoVerificacion.APROBADO) {
            throw new RuntimeException("El conductor no está verificado. Estado: " + conductorInfo.getEstado());
        }

        viaje.setConductorId(conductorId);
        viaje.setConductorNombres(conductor.getNombres());
        viaje.setConductorApellidos(conductor.getApellidos());
        viaje.setConductorCelular(conductor.getCelular());
        viaje.setConductorEmail(conductor.getEmail());
        viaje.setVehiculoPlaca(conductorInfo.getPlaca());
        viaje.setVehiculoMarca(conductorInfo.getMarca());
        viaje.setVehiculoColor(conductorInfo.getColor());
        viaje.setVehiculoAnio(conductorInfo.getAnioFabricacion());
        viaje.setEstado(EstadoViaje.CONDUCTOR_ASIGNADO);

        log.info("Conductor asignado exitosamente: {} {} - Vehículo: {} {} {}",
                conductor.getNombres(),
                conductor.getApellidos(),
                conductorInfo.getMarca(),
                conductorInfo.getColor(),
                conductorInfo.getPlaca());

        return viajeRepository.save(viaje);
    }

    public Optional<Viaje> obtenerViajeActivoConductor(Long conductorId) {
        log.info("Buscando viaje activo para conductor: {}", conductorId);

        List<Viaje> viajes = viajeRepository.findByConductorId(conductorId);

        return viajes.stream()
                .filter(v -> v.getEstado() != EstadoViaje.COMPLETADO
                        && v.getEstado() != EstadoViaje.CANCELADO)
                .findFirst();
    }

    @Transactional
    public Viaje actualizarEstadoViaje(Long viajeId, EstadoViaje nuevoEstado, Long conductorId) {
        log.info("Conductor {} actualizando viaje {} a estado {}", conductorId, viajeId, nuevoEstado);

        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado"));

        if (viaje.getConductorId() == null || !viaje.getConductorId().equals(conductorId)) {
            throw new RuntimeException("Este viaje no pertenece al conductor");
        }

        viaje.setEstado(nuevoEstado);
        Viaje viajeActualizado = viajeRepository.save(viaje);

        if (nuevoEstado == EstadoViaje.COMPLETADO) {
            try {
                enviarBoletaPorEmail(viajeActualizado);
            } catch (Exception e) {
                log.error("Error al enviar boleta por email para viaje {}: {}", viajeId, e.getMessage());
            }
        }

        return viajeActualizado;
    }

    private void enviarBoletaPorEmail(Viaje viaje) {
        try {
            log.info("Generando y enviando boleta para viaje {}", viaje.getId());

            File pdfFile = pdfService.generarBoletaViaje(viaje);
            FileSystemResource pdfResource = new FileSystemResource(pdfFile);

            Map<String, Object> variables = new HashMap<>();
            variables.put("nombre", viaje.getNombre());
            variables.put("apellido", viaje.getApellido());

            mailService.enviarCorreoHtmlConAdjuntos(
                    viaje.getEmailCliente(),
                    "Boleta de Viaje #" + viaje.getId() + " - Viaje Completado",
                    "boleta-viaje.html",
                    variables,
                    new FileSystemResource[]{pdfResource}
            );

            pdfFile.delete();

            log.info("Boleta enviada exitosamente a {}", viaje.getEmailCliente());
        } catch (Exception e) {
            log.error("Error al enviar boleta: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar boleta por email", e);
        }
    }
}