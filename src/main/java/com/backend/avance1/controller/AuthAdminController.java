package com.backend.avance1.controller;

import com.backend.avance1.dto.ApiResponse;
import com.backend.avance1.dto.UserDTO;
import com.backend.avance1.entity.RoleName;
import com.backend.avance1.entity.User;
import com.backend.avance1.security.JwtUtil;
import com.backend.avance1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/admin")
@RequiredArgsConstructor
public class AuthAdminController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        return userService.buscarPorEmail(email)
                .filter(User::isActivo)
                .filter(u -> (u.hasRole(RoleName.ROLE_ADMIN) || u.hasRole(RoleName.ROLE_SUPERADMIN)))
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .map(u -> {
                    String token = jwtUtil.generateToken(u);
                    return ResponseEntity.ok(
                            new ApiResponse(true, "Login exitoso", Map.of(
                                    "token", token,
                                    "roles", u.getRoles()
                            ))
                    );
                })
                .orElse(ResponseEntity.status(401)
                        .body(new ApiResponse(false, "Credenciales inválidas o sin permisos administrativos")));
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping("/register-admin")
    public ResponseEntity<ApiResponse> registerAdmin(@RequestBody UserDTO dto) {
        User admin = User.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .celular(dto.getCelular())
                .email(dto.getEmail())
                .direccion(dto.getDireccion())
                .dniRuc(dto.getDniRuc())
                .password(dto.getPassword())
                .build();

        admin.getRoles().add(RoleName.ROLE_ADMIN);
        admin.setActivo(true);

        userService.registrar(admin);

        return ResponseEntity.ok(new ApiResponse(true, "Administrador creado exitosamente"));
    }
}