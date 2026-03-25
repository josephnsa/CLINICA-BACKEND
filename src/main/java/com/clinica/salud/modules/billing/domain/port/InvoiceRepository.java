package com.clinica.salud.modules.billing.domain.port;

import com.clinica.salud.modules.billing.domain.model.Invoice;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {

    Invoice save(Invoice invoice);

    Optional<Invoice> findById(UUID id);

    int getNextNumberForSerie(String serie);
}
