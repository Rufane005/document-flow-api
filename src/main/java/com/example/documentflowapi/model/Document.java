package com.example.documentflowapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Başlıq boş ola bilməz")
    private String title;
    private String content;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    private String ownerEmail;
    private String approverEmail;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
}