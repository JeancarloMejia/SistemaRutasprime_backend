package com.backend.avance1.repository;

import com.backend.avance1.entity.ConductorInfoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConductorInfoHistorialRepository extends JpaRepository<ConductorInfoHistorial, Long> {
    List<ConductorInfoHistorial> findByConductorInfo_User_IdOrderByFechaCambioDesc(Long userId);
}