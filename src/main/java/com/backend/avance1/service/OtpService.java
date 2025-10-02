package com.backend.avance1.service;

import com.backend.avance1.entity.Otp;
import com.backend.avance1.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpRepository otpRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String generarOtp(String email) {
        List<Otp> previos = otpRepository.findByEmailAndUsadoFalse(email);
        previos.forEach(o -> {
            o.setUsado(true);
            otpRepository.save(o);
        });

        String codigo = String.format("%06d", new Random().nextInt(999999));

        Otp otp = Otp.builder()
                .email(email)
                .codigo(passwordEncoder.encode(codigo))
                .expiracion(LocalDateTime.now().plusMinutes(5))
                .usado(false)
                .build();

        otpRepository.save(otp);

        return codigo;
    }

    public boolean validarOtp(String email, String codigo) {
        List<Otp> otps = otpRepository.findByEmailAndUsadoFalse(email);

        return otps.stream()
                .filter(otp -> otp.getExpiracion().isAfter(LocalDateTime.now()))
                .filter(otp -> passwordEncoder.matches(codigo, otp.getCodigo()))
                .findFirst()
                .map(otp -> {
                    otp.setUsado(true);
                    otpRepository.save(otp);
                    return true;
                })
                .orElse(false);
    }
}