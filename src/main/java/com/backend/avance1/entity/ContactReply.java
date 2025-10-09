package com.backend.avance1.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "contact_replies")
public class ContactReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String replyCode; // Ej: RP-2025-0001-R1
    private String replyMessage;
    private LocalDateTime repliedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "contact_message_id")
    private ContactMessage contactMessage;
}