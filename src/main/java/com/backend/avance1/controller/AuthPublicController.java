package com.backend.avance1.controller;

import com.backend.avance1.dto.ApiResponse;
import com.backend.avance1.dto.ResetPasswordDTO;
import com.backend.avance1.dto.UserDTO;
import com.backend.avance1.entity.RoleName;
import com.backend.avance1.entity.User;
import com.backend.avance1.security.JwtUtil;
import com.backend.avance1.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.backend.avance1.utils.TextFormatUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/public")
@RequiredArgsConstructor
public class AuthPublicController {

    private final UserService userService;
    private final OtpService otpService;
    private final MailService mailService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final DniValidatorService dniValidatorService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserDTO userDto) {
        try {
            boolean coincide = dniValidatorService.validarDatosReniec(userDto);
            if (!coincide) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse(false, "Datos no coinciden con RENIEC")
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }

        userDto.setNombres(TextFormatUtil.capitalizarTexto(userDto.getNombres()));
        userDto.setApellidos(TextFormatUtil.capitalizarTexto(userDto.getApellidos()));
        userDto.setDireccion(TextFormatUtil.capitalizarDireccion(userDto.getDireccion()));

        User user = User.builder()
                .nombres(userDto.getNombres())
                .apellidos(userDto.getApellidos())
                .celular(userDto.getCelular())
                .email(userDto.getEmail())
                .direccion(userDto.getDireccion())
                .dniRuc(userDto.getDniRuc())
                .password(userDto.getPassword())
                .build();

        user.getRoles().add(RoleName.ROLE_CLIENTE);
        user.setActivo(false);

        User nuevo = userService.registrar(user);
        String codigo = otpService.generarOtp(nuevo.getEmail());

        try {
            mailService.enviarCorreoHtml(
                    nuevo.getEmail(),
                    "Confirmación OTP",
                    "otp-register.html",
                    nuevo.getNombres(),
                    codigo
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo de confirmación", e);
        }

        return ResponseEntity.ok(
                new ApiResponse(true, "Usuario registrado. Verifique su correo electrónico")
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String codigo = body.get("codigo");

        if (otpService.validarOtp(email, codigo)) {
            return userService.buscarPorEmail(email)
                    .map(u -> {
                        userService.activarUsuario(u);
                        try {
                            mailService.enviarCorreoHtml(
                                    u.getEmail(),
                                    "¡Bienvenido a Rutas Prime!",
                                    "welcome.html",
                                    u.getNombres(),
                                    ""
                            );
                        } catch (Exception e) {
                            throw new RuntimeException("Error al enviar correo de bienvenida", e);
                        }
                        return ResponseEntity.ok(new ApiResponse(true, "Usuario activado correctamente"));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse(false, "Usuario no encontrado")));
        }

        return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "OTP inválido o expirado"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        return userService.buscarPorEmail(email)
                .filter(User::isActivo)
                .filter(u -> (
                        u.hasRole(RoleName.ROLE_CLIENTE) ||
                                u.hasRole(RoleName.ROLE_CONDUCTOR)
                ))
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
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Credenciales inválidas")));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        return userService.buscarPorEmail(email)
                .map(u -> {
                    String codigo = otpService.generarOtp(email);
                    try {
                        mailService.enviarCorreoHtml(
                                email,
                                "Recuperación de contraseña",
                                "otp-reset.html",
                                u.getNombres(),
                                codigo
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("Error al enviar correo de recuperación", e);
                    }
                    return ResponseEntity.ok(new ApiResponse(true, "OTP enviado al correo"));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "Usuario no encontrado")));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        if (!dto.getNuevaPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Las contraseñas no coinciden"));
        }

        if (!otpService.validarOtp(dto.getEmail(), dto.getCodigo())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "OTP inválido o expirado"));
        }

        return userService.buscarPorEmail(dto.getEmail())
                .map(u -> {
                    if (!u.isActivo()) {
                        return ResponseEntity.badRequest()
                                .body(new ApiResponse(false, "El usuario no ha activado su cuenta"));
                    }

                    if (passwordEncoder.matches(dto.getNuevaPassword(), u.getPassword())) {
                        return ResponseEntity.badRequest()
                                .body(new ApiResponse(false, "La nueva contraseña no puede ser igual a la anterior"));
                    }

                    userService.actualizarPassword(u, dto.getNuevaPassword());
                    return ResponseEntity.ok(new ApiResponse(true, "Contraseña actualizada correctamente"));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "Usuario no encontrado")));
    }

    @PostMapping("/reset-otp")
    public ResponseEntity<ApiResponse> resetOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String type = body.get("type");

        return userService.buscarPorEmail(email)
                .map(user -> {
                    String codigo = otpService.generarOtp(email);
                    try {
                        if ("register".equalsIgnoreCase(type)) {
                            mailService.enviarCorreoHtml(
                                    email,
                                    "Reenvío OTP - Registro",
                                    "otp-register.html",
                                    user.getNombres(),
                                    codigo
                            );
                            return ResponseEntity.ok(new ApiResponse(true, "Nuevo OTP enviado para activación"));
                        }

                        if ("forgot-password".equalsIgnoreCase(type)) {
                            mailService.enviarCorreoHtml(
                                    email,
                                    "Reenvío OTP - Recuperación de contraseña",
                                    "otp-reset.html",
                                    user.getNombres(),
                                    codigo
                            );
                            return ResponseEntity.ok(new ApiResponse(true, "Nuevo OTP enviado para recuperación de contraseña"));
                        }

                        return ResponseEntity.badRequest()
                                .body(new ApiResponse(false, "Tipo de OTP no válido"));
                    } catch (Exception e) {
                        throw new RuntimeException("Error al enviar correo", e);
                    }
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "Usuario no encontrado")));
    }
}