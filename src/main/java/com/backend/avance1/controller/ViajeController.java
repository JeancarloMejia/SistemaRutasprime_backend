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
@RequestMapping("/api/viajes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ViajeController {

    private final ViajeService viajeService;

    @GetMapping("/activo")
    public ResponseEntity<?> obtenerViajeActivo(@RequestParam String email) {
        try {
            System.out.println("=== BUSCANDO VIAJE ACTIVO ===");
            System.out.println("Email: " + email);

            Optional<Viaje> viajeOpt = viajeService.obtenerViajeActivo(email);

            if (viajeOpt.isPresent()) {
                Viaje viaje = viajeOpt.get();
                System.out.println("Viaje activo encontrado: ID " + viaje.getId());

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("tieneViajeActivo", true);
                response.put("viaje", viaje);

                return ResponseEntity.ok(response);
            } else {
                System.out.println("No hay viaje activo para este email");

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

    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorial(@RequestParam String email) {
        try {
            List<Viaje> viajes = viajeService.obtenerHistorial(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("viajes", viajes);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener historial");

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String estadoStr = body.get("estado");
            EstadoViaje nuevoEstado = EstadoViaje.valueOf(estadoStr);

            Viaje viajeActualizado = viajeService.actualizarEstado(id, nuevoEstado);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("viaje", viajeActualizado);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar estado: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerViaje(@PathVariable Long id) {
        try {
            Optional<Viaje> viajeOpt = viajeService.obtenerViajePorId(id);

            if (viajeOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("viaje", viajeOpt.get());

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Viaje no encontrado");

                return ResponseEntity.status(404).body(errorResponse);
            }

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener viaje");

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarViaje(@PathVariable Long id) {
        try {
            Viaje viajeCancelado = viajeService.actualizarEstado(id, EstadoViaje.CANCELADO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Viaje cancelado exitosamente");
            response.put("viaje", viajeCancelado);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al cancelar viaje: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}