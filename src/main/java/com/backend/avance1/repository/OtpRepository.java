package com.backend.avance1.repository;

import com.backend.avance1.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmailAndCodigoAndUsadoFalse(String email, String codigo);
}