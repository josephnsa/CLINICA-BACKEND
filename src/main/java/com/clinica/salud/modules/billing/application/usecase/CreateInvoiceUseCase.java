package com.clinica.salud.modules.billing.application.usecase;

import com.clinica.salud.modules.billing.application.dto.CreateInvoiceRequest;
import com.clinica.salud.modules.billing.application.dto.InvoiceItemRequest;
import com.clinica.salud.modules.billing.application.dto.InvoiceResponse;
import com.clinica.salud.modules.billing.domain.model.Invoice;
import com.clinica.salud.modules.billing.domain.model.InvoiceItem;
import com.clinica.salud.modules.billing.domain.model.InvoiceStatus;
import com.clinica.salud.modules.billing.domain.port.GetServiceNamePort;
import com.clinica.salud.modules.billing.domain.port.GetTariffPricePort;
import com.clinica.salud.modules.billing.domain.port.InvoiceRepository;
import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateInvoiceUseCase {

    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");

    private final InvoiceRepository invoiceRepository;
    private final GetTariffPricePort getTariffPricePort;
    private final GetServiceNamePort getServiceNamePort;

    public InvoiceResponse execute(CreateInvoiceRequest request, UUID createdBy) {
        List<InvoiceItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (InvoiceItemRequest req : request.items()) {
            BigDecimal unitPrice = getTariffPricePort.getUnitPrice(req.serviceId(), request.sedeId())
                    .orElseThrow(() -> new BusinessRuleException("No hay tarifa para el servicio " + req.serviceId() + " en la sede indicada"));
            String description = getServiceNamePort.getServiceName(req.serviceId()).orElse("Servicio");
            int qty = req.quantity() <= 0 ? 1 : req.quantity();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty)).setScale(2, RoundingMode.HALF_UP);
            items.add(InvoiceItem.builder()
                    .serviceId(req.serviceId())
                    .description(description)
                    .quantity(qty)
                    .unitPrice(unitPrice)
                    .total(lineTotal)
                    .build());
            subtotal = subtotal.add(lineTotal);
        }

        BigDecimal tax = subtotal.multiply(IGV_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);

        String serie = "F";
        int number = invoiceRepository.getNextNumberForSerie(serie);
        LocalDateTime now = LocalDateTime.now();

        Invoice invoice = Invoice.builder()
                .serie(serie)
                .number(number)
                .patientId(request.patientId())
                .sedeId(request.sedeId())
                .appointmentId(request.appointmentId())
                .subtotal(subtotal)
                .tax(tax)
                .total(total)
                .status(InvoiceStatus.PENDING)
                .notes(null)
                .createdBy(createdBy)
                .createdAt(now)
                .items(items)
                .payments(new ArrayList<>())
                .build();

        invoice = invoiceRepository.save(invoice);
        return InvoiceResponse.from(invoice);
    }
}
