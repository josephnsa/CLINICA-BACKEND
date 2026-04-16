package com.clinica.salud.modules.billing.infrastructure.persistence;

import com.clinica.salud.modules.billing.domain.model.SunatDocument;
import com.clinica.salud.modules.billing.domain.port.SunatDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SunatDocumentRepositoryAdapter implements SunatDocumentRepository {

    private final SunatDocumentJpaRepository jpa;

    @Override
    public SunatDocument save(SunatDocument doc) {
        SunatDocumentEntity entity = toEntity(doc);
        SunatDocumentEntity saved  = jpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<SunatDocument> findByInvoiceId(UUID invoiceId) {
        return jpa.findByInvoiceId(invoiceId).map(this::toDomain);
    }

    @Override
    public Optional<SunatDocument> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    // ── mapping ──────────────────────────────────────────────────────────────

    private SunatDocumentEntity toEntity(SunatDocument d) {
        SunatDocumentEntity e = new SunatDocumentEntity();
        e.setId(d.getId());
        e.setInvoiceId(d.getInvoiceId());
        e.setDocumentType(d.getDocumentType());
        e.setXmlContent(d.getXmlContent());
        e.setCdrContent(d.getCdrContent());
        e.setTicket(d.getTicket());
        e.setStatus(d.getStatus());
        e.setErrorMessage(d.getErrorMessage());
        e.setSentAt(d.getSentAt());
        e.setCreatedAt(d.getCreatedAt());
        return e;
    }

    private SunatDocument toDomain(SunatDocumentEntity e) {
        SunatDocument d = new SunatDocument();
        d.setId(e.getId());
        d.setInvoiceId(e.getInvoiceId());
        d.setDocumentType(e.getDocumentType());
        d.setXmlContent(e.getXmlContent());
        d.setCdrContent(e.getCdrContent());
        d.setTicket(e.getTicket());
        d.setStatus(e.getStatus());
        d.setErrorMessage(e.getErrorMessage());
        d.setSentAt(e.getSentAt());
        d.setCreatedAt(e.getCreatedAt());
        return d;
    }
}
