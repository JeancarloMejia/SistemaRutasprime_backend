package com.backend.avance1.service;

import com.backend.avance1.dto.EmpresaRegistroDTO;
import com.backend.avance1.utils.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RucValidatorService {

    private static final Logger logger = LoggerFactory.getLogger(RucValidatorService.class);
    private final RucService rucService;

    public RucValidatorService(RucService rucService) {
        this.rucService = rucService;
    }

    public RucValidationResult validarDatosSunat(EmpresaRegistroDTO empresaDto) {
        try {
            Map<String, Object> datosRuc = rucService.consultarRuc(empresaDto.getRucEmpresa());

            String razonSocialApi = datosRuc.getOrDefault("razon_social", "").toString().trim();
            String razonSocialUsuario = empresaDto.getNombreEmpresa().trim();

            String normalizadoApi = TextUtils.normalizarTexto(razonSocialApi);
            String normalizadoUsuario = TextUtils.normalizarTexto(razonSocialUsuario);

            boolean coincide = normalizadoApi.equals(normalizadoUsuario);

            System.out.println("\n========================================");
            System.out.println("[VALIDACIÓN SUNAT - RUC " + empresaDto.getRucEmpresa() + "]");
            if (coincide) {
                System.out.println("✅ Coincide con SUNAT: " + razonSocialApi);
            } else {
                System.out.println("❌ No coincide:");
                System.out.println("→ Usuario: " + razonSocialUsuario);
                System.out.println("→ SUNAT  : " + (razonSocialApi.isEmpty() ? "(vacío o no recibido)" : razonSocialApi));
            }
            System.out.println("========================================\n");

            return new RucValidationResult(coincide, datosRuc);

        } catch (RuntimeException e) {
            logger.error("Error al validar RUC {}: {}", empresaDto.getRucEmpresa(), e.getMessage());
            throw e;
        }
    }

    public record RucValidationResult(boolean coincide, Map<String, Object> datosCompletos) {
    }
}