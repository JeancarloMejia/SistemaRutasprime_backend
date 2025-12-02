package com.backend.avance1.repository;

import com.backend.avance1.entity.EstadoViaje;
import com.backend.avance1.entity.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Long> {

    @Query("SELECT v FROM Viaje v WHERE v.emailCliente = :email AND v.estado IN ('BUSCANDO_CONDUCTOR', 'CONDUCTOR_ASIGNADO', 'EN_CAMINO', 'EN_PROGRESO') ORDER BY v.createdAt DESC")
    Optional<Viaje> findViajeActivoByEmail(@Param("email") String email);

    Optional<Viaje> findByChargeId(String chargeId);

    List<Viaje> findByEstado(EstadoViaje estado);

    @Query("SELECT v FROM Viaje v WHERE v.emailCliente = :email ORDER BY v.createdAt DESC")
    List<Viaje> findHistorialByEmail(@Param("email") String email);

    @Query("SELECT v FROM Viaje v WHERE v.estado = 'BUSCANDO_CONDUCTOR' ORDER BY v.createdAt ASC")
    List<Viaje> findViajesPendientes();

    List<Viaje> findByConductorId(Long conductorId);
}