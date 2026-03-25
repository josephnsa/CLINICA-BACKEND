package com.clinica.salud.modules.billing.application.usecase;

import com.clinica.salud.modules.billing.application.dto.InvoiceResponse;
import com.clinica.salud.modules.billing.domain.port.InvoiceRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetInvoiceUseCase {

    private final InvoiceRepository invoiceRepository;

    public InvoiceResponse execute(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .map(InvoiceResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId.toString()));
    }
}
