package com.clinica.salud.modules.billing.infrastructure.persistence;

import com.clinica.salud.modules.billing.domain.model.SunatDocument;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sunat_documents")
@Getter
@Setter
@NoArgsConstructor
public class SunatDocumentEntity {

    @Id
    private UUID id;

    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @Column(name = "document_type", nullable = false, length = 10)
    private String documentType;

    @Column(name = "xml_content", columnDefinition = "TEXT")
    private String xmlContent;

    @Column(name = "cdr_content", columnDefinition = "TEXT")
    private String cdrContent;

    @Column(name = "ticket", length = 100)
    private String ticket;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SunatDocument.Status status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
