package com.backend.avance1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConductorInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String fechaNacimiento;

    @Column(nullable = false, unique = true)
    private String numeroLicenciaConducir;

    private String fotoPersonaLicencia;
    private String fotoLicencia;
    private String antecedentesPenales;

    @Column(nullable = false)
    private String placa;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String anioFabricacion;

    private String tarjetaPropiedad;
    private String tarjetaCirculacion;
    private String soat;
    private String revisionTecnica;

    @Enumerated(EnumType.STRING)
    private EstadoVerificacion estado = EstadoVerificacion.PENDIENTE;

    @Column(nullable = false)
    private LocalDateTime fechaSolicitud;

    private String observacionAdmin;

    @Column(unique = true, nullable = false)
    private String codigoSolicitud;

    @PrePersist
    public void prePersist() {
        if (fechaSolicitud == null) {
            fechaSolicitud = LocalDateTime.now();
        }
    }
}