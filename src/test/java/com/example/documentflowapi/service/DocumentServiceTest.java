package com.example.documentflowapi.service;

import com.example.documentflowapi.integration.DocumentGateway;
import com.example.documentflowapi.model.Document;
import com.example.documentflowapi.repository.AuditLogRepository;
import com.example.documentflowapi.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentGateway documentGateway;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private DocumentService documentService;

    @Test
    void sened_ugurla_yaradilmali_ve_workflow_bashlamalidir() {
        Document document = new Document();
        document.setTitle("Test Title");
        document.setApproverEmail("test@example.com");
        when(documentRepository.save(any(Document.class))).thenReturn(document);
        Document savedDoc = documentService.submitDocument(document);
        assertNotNull(savedDoc);
        assertEquals("Test Title", savedDoc.getTitle());
        verify(documentRepository, times(1)).save(document);
        verify(documentGateway, times(1)).initiateWorkflow(document);
        verify(auditLogRepository, times(1)).save(any());
    }
}