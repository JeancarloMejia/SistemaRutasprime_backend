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
    private static final String URL = "https://api.codart.cgrt.net/api/v1/consultas/reniec/dni/%s";

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
        String urlFinal = String.format(URL, dni);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(urlFinal, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Error al consultar el servicio de DNI");
        }

        try {
            JsonNode root = objectMapper.readTree(response.getBody());

            if (!root.path("success").asBoolean(false)) {
                throw new RuntimeException("El DNI no existe o no fue encontrado");
            }

            JsonNode result = root.path("result");
            Map<String, String> info = extraerDatos(result);

            logger.info(">>> Datos recibidos de CODART RENIEC API: {}", info);
            return info;

        } catch (Exception e) {
            throw new RuntimeException("Error procesando la respuesta del servicio DNI: " + e.getMessage(), e);
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(security);
        return headers;
    }

    private Map<String, String> extraerDatos(JsonNode result) {
        Map<String, String> info = new HashMap<>();

        String firstName = result.path("first_name").asText("");
        String firstLastName = result.path("first_last_name").asText("");
        String secondLastName = result.path("second_last_name").asText("");

        info.put("nombres", firstName.trim());
        info.put("apellido_paterno", firstLastName.trim());
        info.put("apellido_materno", secondLastName.trim());

        return info;
    }
}