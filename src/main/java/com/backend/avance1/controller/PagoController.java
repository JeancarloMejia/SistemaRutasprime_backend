package com.backend.avance1.controller;

import com.backend.avance1.entity.Viaje;
import com.backend.avance1.service.ViajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PagoController {

    @Value("${culqi.secret.key}")
    private String culqiSecretKey;

    private final ViajeService viajeService;

    @PostMapping("/procesar")
    public ResponseEntity<?> procesarPago(@RequestBody Map<String, Object> requestData) {
        try {
            System.out.println("=== PROCESANDO PAGO ===");
            System.out.println("Datos recibidos: " + requestData);

            String tokenId = (String) requestData.get("token");
            Integer amount = ((Number) requestData.get("amount")).intValue();
            String email = (String) requestData.get("email");
            String firstName = (String) requestData.get("firstName");
            String lastName = (String) requestData.get("lastName");
            String descripcion = (String) requestData.get("descripcion");

            Viaje viaje = new Viaje();
            viaje.setNombre(firstName);
            viaje.setApellido(lastName);
            viaje.setOrigen((String) requestData.get("origen"));
            viaje.setDestino((String) requestData.get("destino"));
            viaje.setOrigenLat(((Number) requestData.get("origenLat")).doubleValue());
            viaje.setOrigenLng(((Number) requestData.get("origenLng")).doubleValue());
            viaje.setDestinoLat(((Number) requestData.get("destinoLat")).doubleValue());
            viaje.setDestinoLng(((Number) requestData.get("destinoLng")).doubleValue());
            viaje.setTipo((String) requestData.get("tipo"));
            viaje.setComentarios((String) requestData.get("comentarios"));
            viaje.setDistanciaKm(((Number) requestData.get("distancia")).doubleValue());
            viaje.setPrecio(amount / 100.0);
            viaje.setEmailCliente(email);

            Map<String, Object> chargeRequest = new HashMap<>();
            chargeRequest.put("amount", amount);
            chargeRequest.put("currency_code", "PEN");
            chargeRequest.put("email", email);
            chargeRequest.put("source_id", tokenId);
            chargeRequest.put("description", descripcion);

            Map<String, Object> antifraudDetails = new HashMap<>();
            antifraudDetails.put("first_name", firstName);
            antifraudDetails.put("last_name", lastName);
            chargeRequest.put("antifraud_details", antifraudDetails);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("nombre", firstName);
            metadata.put("apellido", lastName);
            metadata.put("origen", requestData.get("origen"));
            metadata.put("destino", requestData.get("destino"));
            metadata.put("tipo", requestData.get("tipo"));
            metadata.put("distancia", requestData.get("distancia"));
            chargeRequest.put("metadata", metadata);

            System.out.println("Request a Culqi: " + chargeRequest);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + culqiSecretKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(chargeRequest, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> culqiResponse = restTemplate.exchange(
                    "https://api.culqi.com/v2/charges",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            System.out.println("Respuesta de Culqi: " + culqiResponse.getBody());

            Map<String, Object> culqiData = culqiResponse.getBody();
            String chargeId = (String) culqiData.get("id");
            viaje.setChargeId(chargeId);

            Viaje viajeGuardado = viajeService.crearViaje(viaje);
            System.out.println("Viaje guardado con ID: " + viajeGuardado.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pago procesado correctamente");

            Map<String, Object> data = new HashMap<>();
            data.put("id", chargeId);
            data.put("viajeId", viajeGuardado.getId());
            data.put("amount", amount);
            data.put("estado", viajeGuardado.getEstado().toString());

            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (HttpClientErrorException e) {
            System.err.println("Error HTTP de Culqi: " + e.getStatusCode());
            System.err.println("Body: " + e.getResponseBodyAsString());

            try {
                Map<String, Object> culqiError = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(e.getResponseBodyAsString(), Map.class);

                String declineCode = (String) culqiError.get("decline_code");
                String userMessage = (String) culqiError.get("user_message");
                String merchantMessage = (String) culqiError.get("merchant_message");

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("errorCode", declineCode);
                errorResponse.put("message", userMessage != null ? userMessage : merchantMessage);

                return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
            } catch (Exception parseEx) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Error al procesar el pago");
                return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
            }

        } catch (Exception e) {
            System.err.println("Error general: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al procesar el pago: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}