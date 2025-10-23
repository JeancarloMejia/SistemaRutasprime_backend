package com.backend.avance1.service;

import com.backend.avance1.entity.Otp;
import com.backend.avance1.repository.OtpRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OtpServiceTest extends BaseServiceTest {

    @Mock private OtpRepository otpRepo;
    @Mock private BCryptPasswordEncoder encoder;
    @InjectMocks private OtpService service;

    @Test
    void generarOtp_DeberiaGuardarNuevoCodigo() {
        when(otpRepo.findByEmailAndUsadoFalse(anyString())).thenReturn(List.of());
        when(otpRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(encoder.encode(anyString())).thenReturn("encoded");

        String codigo = service.generarOtp("user@test.com");

        assertThat(codigo).hasSize(6).matches("\\d{6}");
        verify(otpRepo).save(any(Otp.class));
    }

    @Test
    void validarOtp_DeberiaMarcarComoUsado_CuandoCoincide() {
        Otp otp = Otp.builder()
                .email("u@test.com")
                .codigo("encoded")
                .expiracion(LocalDateTime.now().plusMinutes(5))
                .usado(false)
                .build();

        when(otpRepo.findByEmailAndUsadoFalse(anyString())).thenReturn(List.of(otp));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);

        boolean valido = service.validarOtp("u@test.com", "123456");

        assertThat(valido).isTrue();
        verify(otpRepo).save(otp);
    }
}