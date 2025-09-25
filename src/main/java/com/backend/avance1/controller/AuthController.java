package com.backend.avance1.controller;

import com.backend.avance1.entity.User;
import com.backend.avance1.security.JwtUtil;
import com.backend.avance1.service.MailService;
import com.backend.avance1.service.OtpService;
import com.backend.avance1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;
    private final MailService mailService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestBody User user) throws MessagingException, IOException {
        try {
            User nuevo = userService.registrar(user);
            String codigo = otpService.generarOtp(nuevo.getEmail());

           
            mailService.enviarCorreoHtml(
                    nuevo.getEmail(),
                    "Confirmación OTP",
                    "otp-register.html",
                    nuevo.getNombres(),
                    codigo
            );

            return "Usuario registrado. Verifique su correo.";
        } catch (RuntimeException e) {
            
            return e.getMessage();
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String codigo = body.get("codigo");
        boolean valido = otpService.validarOtp(email, codigo);
        return valido ? userService.buscarPorEmail(email)
                .map(u -> { userService.activarUsuario(u); return "Usuario activado"; })
                .orElse("Usuario no encontrado")
                : "OTP inválido o expirado";
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        return userService.buscarPorEmail(body.get("email"))
                .filter(User::isActivo)
                .filter(u -> passwordEncoder.matches(body.get("password"), u.getPassword()))
                .map(u -> {
                    String token = jwtUtil.generateToken(u.getEmail());
                    return Map.of("token", token);
                })
                .orElse(Map.of("error", "Credenciales inválidas"));
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody Map<String, String> body) throws MessagingException, IOException {
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
                        throw new RuntimeException("Error al enviar correo", e);
                    }
                    return "OTP enviado al correo";
                })
                .orElse("Usuario no encontrado");
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String codigo = body.get("codigo");
        String nuevaPassword = body.get("nuevaPassword");
        if (otpService.validarOtp(email, codigo)) {
            return userService.buscarPorEmail(email)
                    .map(u -> { userService.actualizarPassword(u, nuevaPassword); return "Contraseña actualizada"; })
                    .orElse("Usuario no encontrado");
        }
        return "OTP inválido o expirado";
    }
}
