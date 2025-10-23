package com.backend.avance1.service;

import com.backend.avance1.dto.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DniValidatorServiceTest {

    @Mock
    private DniService dniService;

    private DniValidatorService dniValidatorService;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        dniValidatorService = new DniValidatorService(dniService);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void validarDatosReniec_coinciden_nombres_y_apellidos() {
        UserDTO user = new UserDTO();
        user.setDniRuc("12345678");
        user.setNombres("José");
        user.setApellidos("Álvarez Díaz");

        Map<String, String> datos = new HashMap<>();
        datos.put("nombres", "Jose");
        datos.put("apellido_paterno", "Alvarez");
        datos.put("apellido_materno", "Diaz");

        when(dniService.consultarDni("12345678")).thenReturn(datos);

        boolean resultado = dniValidatorService.validarDatosReniec(user);

        assertTrue(resultado);
    }

    @Test
    void validarDatosReniec_datos_no_coinciden() {
        UserDTO user = new UserDTO();
        user.setDniRuc("12345678");
        user.setNombres("Maria");
        user.setApellidos("Perez");

        Map<String, String> datos = new HashMap<>();
        datos.put("nombres", "Jose");
        datos.put("apellido_paterno", "Alvarez");
        datos.put("apellido_materno", "Diaz");

        when(dniService.consultarDni("12345678")).thenReturn(datos);

        boolean resultado = dniValidatorService.validarDatosReniec(user);

        assertFalse(resultado);
    }

    @Test
    void validarDatosReniec_api_falla_lanza_excepcion() {
        UserDTO user = new UserDTO();
        user.setDniRuc("12345678");

        when(dniService.consultarDni("12345678")).thenThrow(new RuntimeException("API fuera de servicio"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> dniValidatorService.validarDatosReniec(user));

        assertTrue(ex.getMessage().contains("Error al validar DNI"));
    }
}