package com.clinica.salud.modules.billing.application.usecase;

import com.clinica.salud.modules.billing.application.dto.SunatStatusResponse;
import com.clinica.salud.modules.billing.domain.model.SunatDocument;
import com.clinica.salud.modules.billing.domain.port.SunatDocumentRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetSunatStatusUseCase {

    private final SunatDocumentRepository sunatDocumentRepository;

    public SunatStatusResponse execute(UUID invoiceId) {
        SunatDocument doc = sunatDocumentRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("SunatDocument", invoiceId.toString()));
        return new SunatStatusResponse(
                doc.getId(),
                doc.getInvoiceId(),
                doc.getDocumentType(),
                doc.getStatus().name(),
                doc.getTicket(),
                doc.getErrorMessage(),
                doc.getSentAt(),
                doc.getCreatedAt()
        );
    }
}
