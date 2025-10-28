package com.backend.avance1.controller;

import com.backend.avance1.dto.UserDTO;
import com.backend.avance1.entity.RoleName;
import com.backend.avance1.entity.User;
import com.backend.avance1.security.JwtUtil;
import com.backend.avance1.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthAdminControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private UserService userService;
    @Mock private JwtUtil jwtUtil;
    @Mock private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthAdminController authAdminController;

    private User admin;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authAdminController).build();

        admin = new User();
        admin.setEmail("admin@test.com");
        admin.setPassword("encodedPass");
        admin.setActivo(true);
        admin.setRoles(Set.of(RoleName.ROLE_ADMIN));
    }

    @Test
    void login_DeberiaRetornarToken_CuandoCredencialesValidasYEsAdmin() throws Exception {
        Map<String, String> body = Map.of(
                "email", "admin@test.com",
                "password", "123456"
        );

        when(userService.buscarPorEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("123456", "encodedPass")).thenReturn(true);
        when(jwtUtil.generateToken(admin)).thenReturn("jwt_token_admin");

        mockMvc.perform(post("/api/auth/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt_token_admin"));
    }

    @Test
    void login_DeberiaFallar_CuandoUsuarioNoEsAdmin() throws Exception {
        User user = new User();
        user.setEmail("client@test.com");
        user.setPassword("encodedPass");
        user.setActivo(true);
        user.setRoles(Set.of(RoleName.ROLE_CLIENTE));

        Map<String, String> body = Map.of(
                "email", "client@test.com",
                "password", "123456"
        );

        when(userService.buscarPorEmail("client@test.com")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void registerAdmin_DeberiaRegistrarAdminCorrectamente() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setNombres("Juan");
        dto.setApellidos("Pérez");
        dto.setCelular("999999999");
        dto.setEmail("nuevoadmin@test.com");
        dto.setDireccion("Av. Siempre Viva 742");
        dto.setDniRuc("12345678");
        dto.setPassword("123456");

        when(userService.registrar(any(User.class))).thenReturn(admin);

        mockMvc.perform(post("/api/auth/admin/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Administrador creado exitosamente"));
    }
}
