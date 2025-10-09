package com.backend.avance1.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String messageCode; // Ej: RP-2025-0001

    private String name;
    private String email;

    @Column(length = 1000)
    private String message;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "contactMessage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactReply> replies;
}