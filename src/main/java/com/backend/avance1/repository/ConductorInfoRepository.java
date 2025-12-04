package com.backend.avance1.repository;

import com.backend.avance1.entity.ConductorInfo;
import com.backend.avance1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConductorInfoRepository extends JpaRepository<ConductorInfo, Long> {
    Optional<ConductorInfo> findByUser(User user);
    List<ConductorInfo> findAllByOrderByFechaSolicitudDesc();

    @Query("SELECT c FROM ConductorInfo c WHERE c.user.id = :userId")
    Optional<ConductorInfo> findByUserId(Long userId);
}