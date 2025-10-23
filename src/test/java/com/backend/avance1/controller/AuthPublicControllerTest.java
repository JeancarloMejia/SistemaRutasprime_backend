package com.backend.avance1.controller;

import com.backend.avance1.dto.ResetPasswordDTO;
import com.backend.avance1.dto.UserDTO;
import com.backend.avance1.entity.RoleName;
import com.backend.avance1.entity.User;
import com.backend.avance1.security.JwtUtil;
import com.backend.avance1.service.MailService;
import com.backend.avance1.service.OtpService;
import com.backend.avance1.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthPublicControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private UserService userService;
    @Mock private OtpService otpService;
    @Mock private MailService mailService;
    @Mock private JwtUtil jwtUtil;
    @Mock private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthPublicController authPublicController;

    private User user;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authPublicController)
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

        user = new User();
        user.setEmail("test@example.com");
        user.setActivo(true);
        user.setPassword("encodedPass");
        user.setRoles(Set.of(RoleName.ROLE_CLIENTE));
    }

    @Test
    void register_DeberiaRegistrarYEnviarCorreo() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setNombres("Juan");
        dto.setApellidos("Pérez");
        dto.setCelular("999999999");
        dto.setEmail("test@example.com");
        dto.setDireccion("Av. Siempre Viva 742");
        dto.setDniRuc("12345678");
        dto.setPassword("12345678");

        when(userService.registrar(any(User.class))).thenReturn(user);
        when(otpService.generarOtp(anyString())).thenReturn("111111");

        mockMvc.perform(post("/api/auth/public/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());

        verify(userService).registrar(any(User.class));
        verify(mailService).enviarCorreoHtml(any(), any(), any(), any(), any());
    }

    @Test
    void verifyOtp_DeberiaActivarUsuario() throws Exception {
        when(otpService.validarOtp(anyString(), anyString())).thenReturn(true);
        when(userService.buscarPorEmail(anyString())).thenReturn(Optional.of(user));

        Map<String, String> body = Map.of("email", "test@example.com", "codigo", "111111");

        mockMvc.perform(post("/api/auth/public/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userService).activarUsuario(any());
        verify(mailService).enviarCorreoHtml(any(), any(), any(), any(), any());
    }

    @Test
    void login_DeberiaRetornarToken_CuandoCredencialesValidas() throws Exception {
        when(userService.buscarPorEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("jwt_token");

        Map<String, String> body = Map.of("email", "test@example.com", "password", "123456");

        mockMvc.perform(post("/api/auth/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("jwt_token"));
    }

    @Test
    void forgotPassword_DeberiaEnviarOtp() throws Exception {
        when(userService.buscarPorEmail(anyString())).thenReturn(Optional.of(user));
        when(otpService.generarOtp(anyString())).thenReturn("111111");

        Map<String, String> body = Map.of("email", "test@example.com");

        mockMvc.perform(post("/api/auth/public/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(mailService).enviarCorreoHtml(any(), any(), any(), any(), any());
    }

    @Test
    void resetPassword_DeberiaActualizarPassword() throws Exception {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setEmail("test@example.com");
        dto.setCodigo("111111");
        dto.setNuevaPassword("nuevaSegura123");
        dto.setConfirmPassword("nuevaSegura123");

        when(otpService.validarOtp(anyString(), anyString())).thenReturn(true);
        when(userService.buscarPorEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/auth/public/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userService).actualizarPassword(any(), anyString());
    }

    @Test
    void resetOtp_DeberiaEnviarCorreoReenvio() throws Exception {
        when(userService.buscarPorEmail(anyString())).thenReturn(Optional.of(user));
        when(otpService.generarOtp(anyString())).thenReturn("123456");

        Map<String, String> body = Map.of("email", "test@example.com", "type", "register");

        mockMvc.perform(post("/api/auth/public/reset-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(mailService).enviarCorreoHtml(any(), any(), any(), any(), any());
    }
}