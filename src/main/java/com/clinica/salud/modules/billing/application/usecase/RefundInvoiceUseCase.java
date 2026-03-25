package com.clinica.salud.modules.billing.application.usecase;

import com.clinica.salud.modules.billing.application.dto.InvoiceResponse;
import com.clinica.salud.modules.billing.domain.model.Invoice;
import com.clinica.salud.modules.billing.domain.port.InvoiceRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefundInvoiceUseCase {

    private final InvoiceRepository invoiceRepository;

    @Transactional
    public InvoiceResponse execute(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId.toString()));
        invoice.refund();
        return InvoiceResponse.from(invoiceRepository.save(invoice));
    }
}
