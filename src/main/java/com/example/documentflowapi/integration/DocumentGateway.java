package com.example.documentflowapi.integration;

import com.example.documentflowapi.model.Document;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface DocumentGateway {

    @Gateway(requestChannel = "documentSubmissionChannel")
    void initiateWorkflow(Document document);
}