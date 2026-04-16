package com.clinica.salud.modules.billing.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SunatDocumentJpaRepository extends JpaRepository<SunatDocumentEntity, UUID> {

    Optional<SunatDocumentEntity> findByInvoiceId(UUID invoiceId);
}
