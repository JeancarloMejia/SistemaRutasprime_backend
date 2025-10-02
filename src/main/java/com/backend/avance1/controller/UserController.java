package com.backend.avance1.controller;

import com.backend.avance1.dto.ApiResponse;
import com.backend.avance1.dto.UpdateUserDTO;
import com.backend.avance1.dto.ChangePasswordDTO;
import com.backend.avance1.entity.User;
import com.backend.avance1.service.MailService;
import com.backend.avance1.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailService mailService;
    private final BCryptPasswordEncoder passwordEncoder;


    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }
        return ResponseEntity.ok(new ApiResponse(true, "Perfil de usuario obtenido", user));
    }


    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateProfile(@AuthenticationPrincipal User user,
                                                     @Valid @RequestBody UpdateUserDTO updatedUser) {
        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        user.setNombres(updatedUser.getNombres());
        user.setApellidos(updatedUser.getApellidos());
        user.setDireccion(updatedUser.getDireccion());

        User savedUser = userService.actualizarUsuario(user);

        try {
            mailService.enviarCorreoHtml(
                    savedUser.getEmail(),
                    "Actualización de perfil",
                    "profile-updated.html",
                    savedUser.getNombres(),
                    ""
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo de actualización de perfil", e);
        }

        return ResponseEntity.ok(new ApiResponse(true, "Perfil actualizado correctamente", savedUser));
    }


    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@AuthenticationPrincipal User user,
                                                      @Valid @RequestBody ChangePasswordDTO dto) {
        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "La contraseña actual es incorrecta"));
        }

        userService.actualizarPassword(user, dto.getNewPassword());

        try {
            mailService.enviarCorreoHtml(
                    user.getEmail(),
                    "Tu contraseña ha sido cambiada",
                    "password-changed.html",
                    user.getNombres(),
                    ""
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo de cambio de contraseña", e);
        }

        return ResponseEntity.ok(new ApiResponse(true, "Contraseña cambiada correctamente"));
    }
}