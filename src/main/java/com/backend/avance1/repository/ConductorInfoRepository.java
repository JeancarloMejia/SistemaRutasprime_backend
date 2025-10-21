package com.backend.avance1.repository;

import com.backend.avance1.entity.ConductorInfo;
import com.backend.avance1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConductorInfoRepository extends JpaRepository<ConductorInfo, Long> {
    Optional<ConductorInfo> findByUser(User user);
}