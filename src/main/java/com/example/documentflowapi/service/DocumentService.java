package com.example.documentflowapi.service;

import com.example.documentflowapi.integration.DocumentGateway;
import com.example.documentflowapi.model.AuditLog;
import com.example.documentflowapi.model.Document;
import com.example.documentflowapi.model.DocumentStatus;
import com.example.documentflowapi.repository.AuditLogRepository;
import com.example.documentflowapi.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final AuditLogRepository auditLogRepository;
    private final DocumentGateway documentGateway;

    @Transactional
    public Document submitDocument(Document document) {
        log.info("Sənəd təqdimetmə prosesi başladı: {}", document.getTitle());

        document.setStatus(DocumentStatus.PENDING_APPROVAL);
        Document savedDoc = documentRepository.save(document);

        auditLogRepository.save(new AuditLog(savedDoc.getId(), "SUBMITTED", "Sənəd sistemə daxil oldu."));

        log.info("Sənəd bazaya yazıldı (ID: {}), integration flow başladılır...", savedDoc.getId());

        documentGateway.initiateWorkflow(savedDoc);

        return savedDoc;
    }

    @Transactional
    public Document approveDocument(Long id) {
        log.info("Sənəd təsdiqlənmə sorğusu gəldi (ID: {})", id);

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Sənəd təsdiqlənərkən xəta: ID {} tapılmadı", id);
                    return new RuntimeException("Sənəd tapılmadı: " + id);
                });

        document.setStatus(DocumentStatus.APPROVED);
        Document updatedDoc = documentRepository.save(document);

        auditLogRepository.save(new AuditLog(id, "APPROVED", "Sənəd menecer tərəfindən təsdiqləndi."));

        log.info("Sənəd uğurla təsdiqləndi (ID: {})", id);

        return updatedDoc;
    }


    @Transactional
    public Document rejectDocument(Long id) {
        log.info("Sənəd imtina sorğusu gəldi (ID: {})", id);

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Sənəd imtina edilərkən xəta: ID {} tapılmadı", id);
                    return new RuntimeException("Sənəd tapılmadı: " + id);
                });

        document.setStatus(DocumentStatus.REJECTED);
        Document updatedDoc = documentRepository.save(document);

        auditLogRepository.save(new AuditLog(id, "REJECTED", "Sənəd menecer tərəfindən imtina edildi."));

        log.info("Sənəd imtina edildi (ID: {})", id);

        return updatedDoc;
    }

    @Transactional(readOnly = true)
    public List<Document> getAllDocuments() {
        log.info("Bütün sənədlər siyahısı tələb olunur");
        return documentRepository.findAll();
    }
}