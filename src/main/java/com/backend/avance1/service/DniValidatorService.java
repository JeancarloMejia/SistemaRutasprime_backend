package com.backend.avance1.service;

import com.backend.avance1.dto.UserDTO;
import com.backend.avance1.utils.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DniValidatorService implements DniValidatorServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(DniValidatorService.class);
    private final DniService dniService;

    public DniValidatorService(DniService dniService) {
        this.dniService = dniService;
    }

    public boolean validarDatosReniec(UserDTO userDto) {
        try {
            Map<String, String> datosDni = dniService.consultarDni(userDto.getDniRuc());

            String nombresApi = datosDni.getOrDefault("nombres", "").trim();
            String apellidoPaterno = datosDni.getOrDefault("apellido_paterno", "").trim();
            String apellidoMaterno = datosDni.getOrDefault("apellido_materno", "").trim();

            String nombresCompletosApi = (nombresApi + " " + apellidoPaterno + " " + apellidoMaterno).trim();
            String nombresCompletosUsuario = (userDto.getNombres() + " " + userDto.getApellidos()).trim();

            String normalizadoApi = TextUtils.normalizarTexto(nombresCompletosApi);
            String normalizadoUsuario = TextUtils.normalizarTexto(nombresCompletosUsuario);

            boolean coincide = normalizadoApi.equals(normalizadoUsuario);

            System.out.println("\n========================================");
            System.out.println("[VALIDACIÓN RENIEC - DNI " + userDto.getDniRuc() + "]");
            if (coincide) {
                System.out.println("✅ Coincide con RENIEC: " + nombresCompletosApi);
            } else {
                System.out.println("❌ No coincide:");
                System.out.println("→ Usuario: " + nombresCompletosUsuario);
                System.out.println("→ RENIEC : " + (nombresCompletosApi.isEmpty() ? "(vacío o no recibido)" : nombresCompletosApi));
            }
            System.out.println("========================================\n");

            return coincide;

        } catch (Exception e) {
            logger.error("Error al validar DNI {}: {}", userDto.getDniRuc(), e.getMessage(), e);
            throw new RuntimeException("Error al validar DNI: " + e.getMessage(), e);
        }
    }
}