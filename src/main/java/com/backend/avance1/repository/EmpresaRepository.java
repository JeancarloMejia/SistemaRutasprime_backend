package com.backend.avance1.repository;

import com.backend.avance1.entity.Empresa;
import com.backend.avance1.entity.EstadoVerificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByRucEmpresa(String rucEmpresa);
    Optional<Empresa> findByCorreoCorporativo(String correoCorporativo);
    Optional<Empresa> findByDni(String dni);
    Optional<Empresa> findByTelefono(String telefono);
    List<Empresa> findAllByOrderByFechaSolicitudDesc();
    List<Empresa> findAllByEstadoOrderByFechaSolicitudDesc(EstadoVerificacion estado);
}