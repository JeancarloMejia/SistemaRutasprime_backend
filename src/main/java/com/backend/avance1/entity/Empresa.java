package com.backend.avance1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "empresas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(unique = true, nullable = false, length = 8)
    private String dni;

    @Column(unique = true, nullable = false)
    private String correoCorporativo;

    @Column(unique = true, nullable = false, length = 9)
    private String telefono;

    @Column(nullable = false)
    private String nombreEmpresa;

    @Column(unique = true, nullable = false, length = 11)
    private String rucEmpresa;

    @Column(nullable = false)
    private String password;

    private boolean activo = false;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "empresa_roles",
            joinColumns = @JoinColumn(name = "empresa_id")
    )
    @Column(name = "role")
    @Builder.Default
    private Set<RoleName> roles = new HashSet<>();

    @Column(unique = true, nullable = false)
    private String codigoSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoVerificacion estado = EstadoVerificacion.PENDIENTE;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String observacionAdmin;

    @Column(columnDefinition = "JSON")
    private String datosSunat;

}