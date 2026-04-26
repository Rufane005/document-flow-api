package com.example.documentflowapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long documentId;
    private String action;
    private String details;
    private LocalDateTime timestamp = LocalDateTime.now();

    public AuditLog(Long documentId, String action, String details) {
        this.documentId = documentId;
        this.action = action;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}