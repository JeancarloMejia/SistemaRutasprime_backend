package com.backend.avance1.repository;

import com.backend.avance1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCelular(String celular);
    Optional<User> findByDniRuc(String dniRuc);
    List<User> findByActivoFalseAndFechaRegistroBefore(LocalDateTime fechaLimite);
}