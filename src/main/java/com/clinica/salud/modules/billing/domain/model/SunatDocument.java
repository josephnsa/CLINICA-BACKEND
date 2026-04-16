package com.clinica.salud.modules.billing.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class SunatDocument {

    public enum Status { PENDING, SENT, ACCEPTED, REJECTED }

    private UUID id;
    private UUID invoiceId;
    private String documentType;   // "01" = factura, "03" = boleta
    private String xmlContent;
    private String cdrContent;
    private String ticket;
    private Status status;
    private String errorMessage;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;

    public SunatDocument() {}

    public SunatDocument(UUID invoiceId, String documentType) {
        this.id           = UUID.randomUUID();
        this.invoiceId    = invoiceId;
        this.documentType = documentType;
        this.status       = Status.PENDING;
        this.createdAt    = LocalDateTime.now();
    }

    public void markSent(String ticket) {
        this.ticket  = ticket;
        this.status  = Status.SENT;
        this.sentAt  = LocalDateTime.now();
    }

    public void markAccepted(String cdrContent) {
        this.cdrContent = cdrContent;
        this.status     = Status.ACCEPTED;
    }

    public void markRejected(String errorMessage) {
        this.errorMessage = errorMessage;
        this.status       = Status.REJECTED;
    }

    public void setXmlContent(String xmlContent) { this.xmlContent = xmlContent; }

    // ── getters ──────────────────────────────────────────────────────────────

    public UUID getId()             { return id; }
    public UUID getInvoiceId()      { return invoiceId; }
    public String getDocumentType() { return documentType; }
    public String getXmlContent()   { return xmlContent; }
    public String getCdrContent()   { return cdrContent; }
    public String getTicket()       { return ticket; }
    public Status getStatus()       { return status; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getSentAt()     { return sentAt; }
    public LocalDateTime getCreatedAt()  { return createdAt; }

    // ── setters for persistence layer ─────────────────────────────────────────

    public void setId(UUID id)                       { this.id = id; }
    public void setInvoiceId(UUID invoiceId)         { this.invoiceId = invoiceId; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public void setCdrContent(String cdrContent)     { this.cdrContent = cdrContent; }
    public void setTicket(String ticket)             { this.ticket = ticket; }
    public void setStatus(Status status)             { this.status = status; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public void setSentAt(LocalDateTime sentAt)      { this.sentAt = sentAt; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }
}
