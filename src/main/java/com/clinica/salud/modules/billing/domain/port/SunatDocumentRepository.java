package com.clinica.salud.modules.billing.domain.port;

import com.clinica.salud.modules.billing.domain.model.SunatDocument;

import java.util.Optional;
import java.util.UUID;

public interface SunatDocumentRepository {

    SunatDocument save(SunatDocument document);

    Optional<SunatDocument> findByInvoiceId(UUID invoiceId);

    Optional<SunatDocument> findById(UUID id);
}
