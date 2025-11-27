package com.backend.avance1.repository;

import com.backend.avance1.entity.EmpresaHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaHistorialRepository extends JpaRepository<EmpresaHistorial, Long> {
    List<EmpresaHistorial> findByEmpresa_IdOrderByFechaCambioDesc(Long empresaId);
}