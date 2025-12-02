package com.backend.avance1.service;

import com.backend.avance1.entity.EstadoViaje;
import com.backend.avance1.entity.Viaje;
import com.backend.avance1.repository.ViajeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViajeService {

    private final ViajeRepository viajeRepository;

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

    // MÉTODOS PARA CONDUCTORES

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

        viaje.setConductorId(conductorId);
        viaje.setEstado(EstadoViaje.CONDUCTOR_ASIGNADO);

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
        return viajeRepository.save(viaje);
    }
}