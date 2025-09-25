package com.backend.avance1.service;

import com.backend.avance1.entity.Otp;
import com.backend.avance1.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpRepository otpRepository;

    public String generarOtp(String email) {
        String codigo = String.format("%06d", new Random().nextInt(999999));
        Otp otp = Otp.builder()
                .email(email)
                .codigo(codigo)
                .expiracion(LocalDateTime.now().plusMinutes(5))
                .build();
        otpRepository.save(otp);
        return codigo;
    }

    public boolean validarOtp(String email, String codigo) {
        return otpRepository.findByEmailAndCodigoAndUsadoFalse(email, codigo)
                .filter(o -> o.getExpiracion().isAfter(LocalDateTime.now()))
                .map(o -> {
                    o.setUsado(true);
                    otpRepository.save(o);
                    return true;
                })
                .orElse(false);
    }
}