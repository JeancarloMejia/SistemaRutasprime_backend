package com.backend.avance1.controller;

import com.backend.avance1.dto.ApiResponse;
import com.backend.avance1.dto.ConductorInfoDTO;
import com.backend.avance1.dto.ConductorInfoResponseDTO;
import com.backend.avance1.entity.ConductorInfo;
import com.backend.avance1.entity.EstadoVerificacion;
import com.backend.avance1.service.ConductorInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ConductorControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ConductorInfoService conductorInfoService;

    @InjectMocks
    private ConductorController conductorController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(conductorController)
                .setValidator(new Validator() {
                    @Override
                    public boolean supports(@NonNull Class<?> clazz) {
                        return true;
                    }

                    @Override
                    public void validate(@NonNull Object target, @NonNull Errors errors) {
                    }
                })
                .build();
    }

    @Test
    void apply_DeberiaRegistrarSolicitud() throws Exception {
        String email = "user@example.com";

        ConductorInfoDTO dto = new ConductorInfoDTO();
        dto.setFechaNacimiento("1990-01-01");
        dto.setNumeroLicenciaConducir("ABC1234");
        dto.setPlaca("XYZ123");
        dto.setMarca("Toyota");
        dto.setColor("Rojo");
        dto.setAnioFabricacion("2020");

        MultipartFile mockFile = mock(MultipartFile.class);

        ConductorInfo returnedInfo = new ConductorInfo();
        returnedInfo.setId(1L);

        when(conductorInfoService.registrarSolicitud(
                eq(email),
                any(ConductorInfoDTO.class),
                any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(returnedInfo);

        mockMvc.perform(multipart("/api/conductor/apply")
                        .file("fotoPersonaLicencia", new byte[0])
                        .file("fotoLicencia", new byte[0])
                        .file("antecedentesPenales", new byte[0])
                        .file("tarjetaPropiedad", new byte[0])
                        .file("tarjetaCirculacion", new byte[0])
                        .file("soat", new byte[0])
                        .file("revisionTecnica", new byte[0])
                        .param("fechaNacimiento", dto.getFechaNacimiento())
                        .param("numeroLicenciaConducir", dto.getNumeroLicenciaConducir())
                        .param("placa", dto.getPlaca())
                        .param("marca", dto.getMarca())
                        .param("color", dto.getColor())
                        .param("anioFabricacion", dto.getAnioFabricacion())
                        .param("email", email)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(conductorInfoService).registrarSolicitud(
                eq(email),
                any(ConductorInfoDTO.class),
                any(), any(), any(), any(), any(), any(), any()
        );
    }

    @Test
    void getStatus_DeberiaRetornarEstado() throws Exception {
        String email = "user@example.com";

        ConductorInfoResponseDTO dto = new ConductorInfoResponseDTO();
        dto.setId(1L);
        dto.setEstado(EstadoVerificacion.PENDIENTE);

        when(conductorInfoService.obtenerEstadoPorEmail(email)).thenReturn(dto);

        mockMvc.perform(get("/api/conductor/status")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(conductorInfoService).obtenerEstadoPorEmail(email);
    }

    @Test
    void getStatus_DeberiaRetornarMensajeSiNoHaySolicitud() throws Exception {
        String email = "user@example.com";

        when(conductorInfoService.obtenerEstadoPorEmail(email)).thenReturn(null);

        mockMvc.perform(get("/api/conductor/status")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(conductorInfoService).obtenerEstadoPorEmail(email);
    }

    @Test
    void verify_DeberiaActualizarEstado() throws Exception {
        Long id = 1L;
        Map<String, String> body = new HashMap<>();
        body.put("estado", "APROBADO");
        body.put("observacion", "Todo ok");

        ConductorInfoResponseDTO dto = new ConductorInfoResponseDTO();
        dto.setId(id);
        dto.setEstado(EstadoVerificacion.APROBADO);

        when(conductorInfoService.actualizarEstado(id, EstadoVerificacion.APROBADO, "Todo ok"))
                .thenReturn(dto);

        mockMvc.perform(put("/api/conductor/verify/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(conductorInfoService).actualizarEstado(id, EstadoVerificacion.APROBADO, "Todo ok");
    }
}
