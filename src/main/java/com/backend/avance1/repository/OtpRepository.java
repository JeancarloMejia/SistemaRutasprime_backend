package com.backend.avance1.repository;

import com.backend.avance1.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    List<Otp> findByEmailAndUsadoFalse(String email);
}