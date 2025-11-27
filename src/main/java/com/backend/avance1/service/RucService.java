package com.backend.avance1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RucService {

    private static final Logger logger = LoggerFactory.getLogger(RucService.class);
    private static final String URL_BASE = "https://api.codart.cgrt.net/api/v1/consultas/sunat/ruc/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ruc.bearer.token}")
    private String bearerToken;

    public RucService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, Object> consultarRuc(String ruc) {
        HttpHeaders headers = buildHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = URL_BASE + ruc;

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("El RUC no existe");
            }

            JsonNode root = objectMapper.readTree(response.getBody());

            if (!root.path("success").asBoolean(false)) {
                throw new RuntimeException("El RUC no existe");
            }

            JsonNode result = root.path("result");
            Map<String, Object> infoCompleta = extraerDatosCompletos(result);

            logger.info(">>> Datos recibidos de SUNAT API para RUC {}: {}", ruc, infoCompleta.get("razon_social"));
            return infoCompleta;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("El RUC no existe");
            }
            throw new RuntimeException("El RUC no existe");
        } catch (Exception e) {
            if (e.getMessage().contains("RUC no encontrado")) {
                throw new RuntimeException("El RUC no existe");
            }
            throw new RuntimeException("Error al validar RUC: " + e.getMessage());
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + bearerToken);
        return headers;
    }

    private Map<String, Object> extraerDatosCompletos(JsonNode result) {
        Map<String, Object> info = new HashMap<>();

        info.put("razon_social", result.path("razon_social").asText(""));
        info.put("tipo_documento", result.path("tipo_documento").asText(""));
        info.put("numero_documento", result.path("numero_documento").asText(""));
        info.put("estado", result.path("estado").asText(""));
        info.put("condicion", result.path("condicion").asText(""));
        info.put("direccion", result.path("direccion").asText(""));
        info.put("ubigeo", result.path("ubigeo").asText(""));
        info.put("via_tipo", result.path("via_tipo").asText(""));
        info.put("via_nombre", result.path("via_nombre").asText(""));
        info.put("zona_codigo", result.path("zona_codigo").asText(""));
        info.put("zona_tipo", result.path("zona_tipo").asText(""));
        info.put("numero", result.path("numero").asText(""));
        info.put("interior", result.path("interior").asText(""));
        info.put("lote", result.path("lote").asText(""));
        info.put("dpto", result.path("dpto").asText(""));
        info.put("manzana", result.path("manzana").asText(""));
        info.put("kilometro", result.path("kilometro").asText(""));
        info.put("distrito", result.path("distrito").asText(""));
        info.put("provincia", result.path("provincia").asText(""));
        info.put("departamento", result.path("departamento").asText(""));
        info.put("es_agente_retencion", result.path("es_agente_retencion").asBoolean(false));
        info.put("es_buen_contribuyente", result.path("es_buen_contribuyente").asBoolean(false));
        info.put("locales_anexos", result.path("locales_anexos").asText(""));
        info.put("tipo", result.path("tipo").asText(""));
        info.put("actividad_economica", result.path("actividad_economica").asText(""));
        info.put("numero_trabajadores", result.path("numero_trabajadores").asText(""));
        info.put("tipo_facturacion", result.path("tipo_facturacion").asText(""));
        info.put("tipo_contabilidad", result.path("tipo_contabilidad").asText(""));
        info.put("comercio_exterior", result.path("comercio_exterior").asText(""));

        return info;
    }
}