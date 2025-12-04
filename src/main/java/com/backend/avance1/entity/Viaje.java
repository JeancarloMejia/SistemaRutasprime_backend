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

    // Datos del cliente
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(name = "email_cliente")
    private String emailCliente;

    // Datos del viaje
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

    // Datos financieros
    @Column(nullable = false)
    private Double precio;

    @Column(name = "ganancia_conductor")
    private Double gananciaConductor;

    @Column(name = "charge_id")
    private String chargeId;

    // Estado del viaje
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoViaje estado;

    //  Datos del conductor (desnormalizados)
    @Column(name = "conductor_id")
    private Long conductorId;

    @Column(name = "conductor_nombres")
    private String conductorNombres;

    @Column(name = "conductor_apellidos")
    private String conductorApellidos;

    @Column(name = "conductor_celular")
    private String conductorCelular;

    @Column(name = "conductor_email")
    private String conductorEmail;

    //  Datos del vehículo del conductor
    @Column(name = "vehiculo_placa")
    private String vehiculoPlaca;

    @Column(name = "vehiculo_marca")
    private String vehiculoMarca;

    @Column(name = "vehiculo_color")
    private String vehiculoColor;

    @Column(name = "vehiculo_anio")
    private String vehiculoAnio;

    // Timestamps
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