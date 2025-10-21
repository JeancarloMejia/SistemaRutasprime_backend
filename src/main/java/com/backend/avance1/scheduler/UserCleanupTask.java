package com.backend.avance1.scheduler;

import com.backend.avance1.repository.UserRepository;
import com.backend.avance1.repository.OtpRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCleanupTask {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void eliminarUsuariosNoVerificados() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(2);

        var usuariosPendientes = userRepository.findByActivoFalseAndFechaRegistroBefore(limite);

        if (!usuariosPendientes.isEmpty()) {
            usuariosPendientes.forEach(u -> {
                otpRepository.deleteByEmail(u.getEmail());
                userRepository.delete(u);
                log.info("Usuario no verificado eliminado: {}", u.getEmail());
            });
            log.info("{} usuarios inactivos eliminados automáticamente", usuariosPendientes.size());
        }
    }
}