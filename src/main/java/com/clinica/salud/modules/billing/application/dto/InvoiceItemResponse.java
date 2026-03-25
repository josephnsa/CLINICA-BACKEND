package com.clinica.salud.modules.billing.application.dto;

import com.clinica.salud.modules.billing.domain.model.InvoiceItem;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceItemResponse(
        UUID id,
        UUID serviceId,
        String description,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal total
) {
    public static InvoiceItemResponse from(InvoiceItem item) {
        return new InvoiceItemResponse(
                item.getId(),
                item.getServiceId(),
                item.getDescription(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotal()
        );
    }
}
