package com.backend.avance1.service;

import com.backend.avance1.dto.ConductorInfoDTO;
import com.backend.avance1.entity.*;
import com.backend.avance1.repository.ConductorInfoRepository;
import com.backend.avance1.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConductorInfoServiceTest {

    @Mock private ConductorInfoRepository conductorRepo;
    @Mock private UserRepository userRepo;
    @Mock private UserService userService;
    @Mock private FileStorageService fileService;
    @Mock private MailService mailService;
    @InjectMocks private ConductorInfoService service;

    private User user;
    private ConductorInfoDTO dto;
    private MultipartFile mockFile;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("user@test.com");
        user.setActivo(true);
        user.setDniRuc("12345678");
        user.setRoles(Set.of(RoleName.ROLE_CLIENTE));

        dto = new ConductorInfoDTO();
        dto.setNumeroLicenciaConducir("A123");
        dto.setPlaca("XYZ123");

        mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("foto.jpg");
        when(fileService.guardarArchivo(any(), any(), any())).thenReturn("/fake/path/foto.jpg");

        when(userRepo.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(conductorRepo.findByUser(user)).thenReturn(Optional.empty());
        when(conductorRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void registrarSolicitud_DeberiaGuardarYEnviarCorreo() throws Exception {
        ConductorInfo info = service.registrarSolicitud(
                user.getEmail(),
                dto,
                mockFile, mockFile, mockFile,
                mockFile, mockFile, mockFile, mockFile
        );

        assertThat(info).isNotNull();
        verify(fileService, times(7)).guardarArchivo(any(), any(), any());
        verify(mailService, atLeastOnce()).enviarCorreoHtmlConAdjuntos(any(), any(), any(), any(), any());
    }


    @Test
    void registrarSolicitud_DeberiaLanzarExcepcion_SiUsuarioInactivo() {
        user.setActivo(false);
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.registrarSolicitud(
                user.getEmail(), dto, mockFile, mockFile, mockFile,
                mockFile, mockFile, mockFile, mockFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("activo");
    }
}