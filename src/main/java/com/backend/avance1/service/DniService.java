package com.backend.avance1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class DniService {

    private static final Logger logger = LoggerFactory.getLogger(DniService.class);
    private static final String URL = "https://dniperu.com/wp-admin/admin-ajax.php";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${dni.security}")
    private String security;

    public DniService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, String> consultarDni(String dni) {
        HttpHeaders headers = buildHeaders();
        String body = String.format("dni4=%s&company=&action=buscar_nombres&security=%s", dni, security);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Error al consultar el servicio de DNI");
        }

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            if (!root.path("success").asBoolean(false)) {
                throw new RuntimeException("El DNI no existe o no fue encontrado");
            }

            JsonNode data = root.path("data");
            Map<String, String> info = extraerDatos(data);

            logger.info(">>> Datos recibidos de RENIEC API: {}", info);
            return info;

        } catch (Exception e) {
            throw new RuntimeException("Error procesando la respuesta del servicio DNI: " + e.getMessage(), e);
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        headers.set("Accept", "*/*");
        headers.set("Origin", "https://dniperu.com");
        headers.set("Referer", "https://dniperu.com/buscar-dni-nombres-apellidos/");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private Map<String, String> extraerDatos(JsonNode data) {
        Map<String, String> info = new HashMap<>();

        String nombres = data.path("nombres").asText("");
        String apellidoPaterno = data.path("apellido_paterno").asText("");
        String apellidoMaterno = data.path("apellido_materno").asText("");

        if ((nombres + apellidoPaterno + apellidoMaterno).isBlank()) {
            String mensaje = data.path("message").asText("");
            if (!mensaje.isBlank()) {
                Map<String, String> parsed = parsearMensaje(mensaje);
                nombres = parsed.getOrDefault("nombres", "");
                apellidoPaterno = parsed.getOrDefault("apellido_paterno", "");
                apellidoMaterno = parsed.getOrDefault("apellido_materno", "");
            }
        }

        info.put("nombres", nombres.trim());
        info.put("apellido_paterno", apellidoPaterno.trim());
        info.put("apellido_materno", apellidoMaterno.trim());

        return info;    
    }

    private Map<String, String> parsearMensaje(String mensaje) {
        Map<String, String> resultado = new HashMap<>();

        for (String linea : mensaje.split("\n")) {
            String[] partes = linea.split(":", 2);
            if (partes.length < 2) continue;

            String clave = partes[0].trim().toLowerCase();
            String valor = partes[1].trim();

            switch (clave) {
                case "nombres" -> resultado.put("nombres", valor);
                case "apellido paterno" -> resultado.put("apellido_paterno", valor);
                case "apellido materno" -> resultado.put("apellido_materno", valor);
                default -> {
                }
            }
        }
        return resultado;
    }
}