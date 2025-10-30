package com.backend.avance1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ConductorInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Relación configurada para serializar solo el ID, sin tocar el proxy
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Evita que Jackson intente serializar el proxy completo
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

    // ✅ Campo adicional para exponer solo el ID del usuario
    @JsonProperty("user")
    public Long getUserId() {
        return (user != null) ? user.getId() : null;
    }

    @PrePersist
    public void prePersist() {
        if (fechaSolicitud == null) {
            fechaSolicitud = LocalDateTime.now();
        }
    }
}