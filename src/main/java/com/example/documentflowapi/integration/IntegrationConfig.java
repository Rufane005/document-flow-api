package com.example.documentflowapi.integration;

import com.example.documentflowapi.model.Document;
import com.example.documentflowapi.model.DocumentStatus;
import com.example.documentflowapi.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.MessageChannel;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.policy.SimpleRetryPolicy;
import java.time.LocalDateTime;

@Configuration
@Slf4j
public class IntegrationConfig {

    @Bean
    public MessageChannel documentSubmissionChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel emailNotificationChannel() {
        return new DirectChannel();
    }

    @Bean
    public RequestHandlerRetryAdvice retryAdvice() {
        RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        policy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(policy);
        advice.setRetryTemplate(retryTemplate);
        return advice;
    }

    @Bean
    public IntegrationFlow documentFlow(DocumentRepository repository, JavaMailSender mailSender) {
        return IntegrationFlow.from("documentSubmissionChannel")
                .handle(Document.class, (doc, headers) -> {
                    doc.setStatus(DocumentStatus.PENDING_APPROVAL);
                    doc.setUpdatedAt(LocalDateTime.now());
                    return repository.save(doc);
                })
                .channel("emailNotificationChannel")
                .handle(Document.class, (doc, headers) -> {
                    SimpleMailMessage mail = new SimpleMailMessage();
                    mail.setTo(doc.getApproverEmail());
                    mail.setSubject("Yeni Sənəd Təsdiq Gözləyir: " + doc.getTitle());
                    mail.setText("Salam, zəhmət olmasa '" + doc.getTitle() + "' sənədini təsdiqləyin.");

                    mailSender.send(mail);
                    log.info("E-mail göndərildi: {}", doc.getApproverEmail());
                    return doc;
                }, c -> c.advice(retryAdvice()))
                .get();
    }
}