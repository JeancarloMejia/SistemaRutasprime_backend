package com.backend.avance1.controller;

import com.backend.avance1.entity.EstadoViaje;
import com.backend.avance1.entity.Viaje;
import com.backend.avance1.service.ViajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/driver")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DriverController {

    private final ViajeService viajeService;

    @GetMapping("/available-trips")
    public ResponseEntity<?> obtenerViajesDisponibles() {
        try {
            System.out.println("=== BUSCANDO VIAJES DISPONIBLES ===");

            List<Viaje> viajesDisponibles = viajeService.obtenerViajesDisponibles();

            System.out.println("Viajes encontrados: " + viajesDisponibles.size());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hayViajes", !viajesDisponibles.isEmpty());
            response.put("viajes", viajesDisponibles);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error al buscar viajes disponibles: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al buscar viajes disponibles");

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/accept-trip")
    public ResponseEntity<?> aceptarViaje(@RequestBody Map<String, Object> request) {
        try {
            Long viajeId = ((Number) request.get("viajeId")).longValue();
            Long driverId = ((Number) request.get("driverId")).longValue();

            System.out.println("=== CONDUCTOR ACEPTANDO VIAJE ===");
            System.out.println("Viaje ID: " + viajeId);
            System.out.println("Driver ID: " + driverId);

            Viaje viajeAsignado = viajeService.asignarViajeAConductor(viajeId, driverId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Viaje aceptado exitosamente");
            response.put("viaje", viajeAsignado);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("Error al aceptar viaje: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            System.err.println("Error general al aceptar viaje: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al aceptar el viaje");

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/active-trip")
    public ResponseEntity<?> obtenerViajeActivo(@RequestParam Long driverId) {
        try {
            System.out.println("=== BUSCANDO VIAJE ACTIVO DEL CONDUCTOR ===");
            System.out.println("Driver ID: " + driverId);

            Optional<Viaje> viajeOpt = viajeService.obtenerViajeActivoConductor(driverId);

            if (viajeOpt.isPresent()) {
                Viaje viaje = viajeOpt.get();
                System.out.println("Viaje activo encontrado: ID " + viaje.getId());

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("tieneViajeActivo", true);
                response.put("viaje", viaje);

                return ResponseEntity.ok(response);
            } else {
                System.out.println("No hay viaje activo para este conductor");

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("tieneViajeActivo", false);
                response.put("viaje", null);

                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            System.err.println("Error al buscar viaje activo: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al buscar viaje activo");

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @PutMapping("/update-status")
    public ResponseEntity<?> actualizarEstadoViaje(@RequestBody Map<String, Object> request) {
        try {
            Long viajeId = ((Number) request.get("viajeId")).longValue();
            Long driverId = ((Number) request.get("driverId")).longValue();
            String estadoStr = (String) request.get("estado");

            EstadoViaje nuevoEstado = EstadoViaje.valueOf(estadoStr);

            System.out.println("=== ACTUALIZANDO ESTADO DEL VIAJE ===");
            System.out.println("Viaje ID: " + viajeId);
            System.out.println("Nuevo estado: " + nuevoEstado);

            Viaje viajeActualizado = viajeService.actualizarEstadoViaje(viajeId, nuevoEstado, driverId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estado actualizado exitosamente");
            response.put("viaje", viajeActualizado);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar estado: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}