package com.backend.avance1.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "viajes")
@Data
public class Viaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, length = 500)
    private String origen;

    @Column(nullable = false, length = 500)
    private String destino;

    @Column(nullable = false)
    private Double origenLat;

    @Column(nullable = false)
    private Double origenLng;

    @Column(nullable = false)
    private Double destinoLat;

    @Column(nullable = false)
    private Double destinoLng;

    @Column(nullable = false)
    private String tipo;

    @Column(length = 1000)
    private String comentarios;

    @Column(nullable = false)
    private Double distanciaKm;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoViaje estado;

    @Column(name = "charge_id")
    private String chargeId;

    @Column(name = "email_cliente")
    private String emailCliente;

    @Column(name = "conductor_id")
    private Long conductorId;

    @Column(name = "ganancia_conductor")
    private Double gananciaConductor;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoViaje.BUSCANDO_CONDUCTOR;
        }
        if (precio != null && gananciaConductor == null) {
            gananciaConductor = precio * 0.8;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}