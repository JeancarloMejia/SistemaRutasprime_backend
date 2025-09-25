package com.backend.avance1.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombres;
    private String apellidos;
    @Column(unique = true, nullable = false)
    private String celular;
    @Column(unique = true, nullable = false)
    private String email;
    private String direccion;
    @Column(unique = true, nullable = false)
    private String dniRuc;
    private String password;
    private boolean activo = false;
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}