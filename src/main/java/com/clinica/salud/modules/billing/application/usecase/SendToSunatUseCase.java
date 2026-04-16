package com.clinica.salud.modules.billing.application.usecase;

import com.clinica.salud.modules.billing.application.dto.SunatStatusResponse;
import com.clinica.salud.modules.billing.domain.model.SunatDocument;
import com.clinica.salud.modules.billing.domain.port.SunatDocumentRepository;
import com.clinica.salud.modules.billing.infrastructure.sunat.NubefactPseClient;
import com.clinica.salud.modules.billing.infrastructure.sunat.UblXmlBuilder;
import com.clinica.salud.modules.billing.infrastructure.sunat.XmlDigitalSigner;
import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

/**
 * Orquesta el flujo completo: genera XML → firma → envía al PSE → persiste estado.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SendToSunatUseCase {

    private final SunatDocumentRepository sunatDocumentRepository;
    private final UblXmlBuilder ublXmlBuilder;
    private final XmlDigitalSigner xmlDigitalSigner;
    private final NubefactPseClient nubefactPseClient;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public SunatStatusResponse execute(UUID invoiceId) {
        // Verificar que la factura existe y está en estado válido para envío
        Map<String, Object> invoice = jdbcTemplate.queryForMap(
                "SELECT serie, number, status FROM invoices WHERE id = ?", invoiceId);
        String invoiceStatus = String.valueOf(invoice.get("status"));
        if (!"PAID".equalsIgnoreCase(invoiceStatus) && !"PENDING".equalsIgnoreCase(invoiceStatus)) {
            throw new BusinessRuleException("Solo se pueden enviar a SUNAT facturas en estado PAID o PENDING");
        }

        // Verificar si ya hay un documento SUNAT aceptado
        sunatDocumentRepository.findByInvoiceId(invoiceId).ifPresent(existing -> {
            if (SunatDocument.Status.ACCEPTED == existing.getStatus()) {
                throw new BusinessRuleException("Este comprobante ya fue aceptado por SUNAT");
            }
        });

        String serie  = String.valueOf(invoice.get("serie"));
        int    number = ((Number) invoice.get("number")).intValue();
        String docType = serie.startsWith("B") ? "03" : "01";

        // Crear o reutilizar el registro
        SunatDocument doc = sunatDocumentRepository.findByInvoiceId(invoiceId)
                .orElse(new SunatDocument(invoiceId, docType));

        try {
            // 1. Generar XML UBL 2.1
            String xml = ublXmlBuilder.build(invoiceId);
            doc.setXmlContent(xml);

            // 2. Firmar digitalmente
            String signedXml = xmlDigitalSigner.sign(xml);

            // 3. Enviar al PSE
            String cdr = nubefactPseClient.send(signedXml, docType, serie, number);
            doc.markAccepted(cdr);
            log.info("Comprobante {}-{} aceptado por SUNAT", serie, number);

        } catch (Exception e) {
            log.error("Error enviando comprobante a SUNAT: {}", e.getMessage());
            doc.markRejected(e.getMessage());
        }

        SunatDocument saved = sunatDocumentRepository.save(doc);
        return toResponse(saved);
    }

    private SunatStatusResponse toResponse(SunatDocument d) {
        return new SunatStatusResponse(
                d.getId(),
                d.getInvoiceId(),
                d.getDocumentType(),
                d.getStatus().name(),
                d.getTicket(),
                d.getErrorMessage(),
                d.getSentAt(),
                d.getCreatedAt()
        );
    }
}
