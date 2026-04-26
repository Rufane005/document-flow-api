package com.example.documentflowapi.controller;

import com.example.documentflowapi.model.AuditLog;
import com.example.documentflowapi.model.Document;
import com.example.documentflowapi.repository.AuditLogRepository;
import com.example.documentflowapi.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final AuditLogRepository auditLogRepository;

    @GetMapping("/{id}/history")
    public List<AuditLog> getHistory(@PathVariable Long id) {
        return auditLogRepository.findByDocumentId(id);
    }

    @PostMapping
    public ResponseEntity<Document> submit(@Valid @RequestBody Document document) {
        return ResponseEntity.ok(documentService.submitDocument(document));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Document> approve(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.approveDocument(id));
    }

    @GetMapping
    public List<Document> getAll() {
        return documentService.getAllDocuments();
    }
    @PutMapping("/{id}/reject")
    public ResponseEntity<Document> reject(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.rejectDocument(id));
    }
}