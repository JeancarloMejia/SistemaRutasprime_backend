package com.backend.avance1.controller;

import com.backend.avance1.dto.ApiResponse;
import com.backend.avance1.dto.UpdateUserDTO;
import com.backend.avance1.dto.ChangePasswordDTO;
import com.backend.avance1.entity.User;
import com.backend.avance1.service.MailService;
import com.backend.avance1.service.UserService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private MailService mailService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .nombres("Juan")
                .apellidos("Perez")
                .email("test@example.com")
                .password("hashedPassword")
                .direccion("Av. Lima 123")
                .celular("987654321")
                .dniRuc("12345678")
                .activo(true)
                .build();
    }

    @Test
    void testGetProfile_Success() {
        ResponseEntity<ApiResponse> response = userController.getProfile(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals(user, response.getBody().getData());
    }

    @Test
    void testUpdateProfile_Success() throws Exception {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setNombres("Juan");
        dto.setApellidos("Perez");
        dto.setDireccion("Calle Falsa 456");

        when(userService.actualizarUsuario(user)).thenReturn(user);

        ResponseEntity<ApiResponse> response = userController.updateProfile(user, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(true, response.getBody().isSuccess());

        verify(userService, times(1)).actualizarUsuario(user);
        verify(mailService, times(1))
                .enviarCorreoHtml(eq(user.getEmail()), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testChangePassword_Success() throws Exception {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setOldPassword("old");
        dto.setNewPassword("NewPass1!");

        when(passwordEncoder.matches("old", user.getPassword())).thenReturn(true);

        ResponseEntity<ApiResponse> response = userController.changePassword(user, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(true, response.getBody().isSuccess());

        verify(userService, times(1)).actualizarPassword(user, "NewPass1!");
        verify(mailService, times(1))
                .enviarCorreoHtml(eq(user.getEmail()), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testChangePassword_WrongOldPassword() throws MessagingException, IOException {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setOldPassword("wrongOld");
        dto.setNewPassword("NewPass1!");

        when(passwordEncoder.matches("wrongOld", user.getPassword())).thenReturn(false);

        ResponseEntity<ApiResponse> response = userController.changePassword(user, dto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals(false, response.getBody().isSuccess());

        verify(userService, never()).actualizarPassword(any(), any());
        verify(mailService, never()).enviarCorreoHtml(anyString(), anyString(), anyString(), anyString(), anyString());
    }
}
