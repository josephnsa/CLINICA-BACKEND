package com.clinica.salud.modules.billing.application.dto;

import com.clinica.salud.modules.billing.domain.model.Payment;
import com.clinica.salud.modules.billing.domain.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID invoiceId,
        PaymentMethod method,
        BigDecimal amount,
        String reference,
        LocalDateTime paidAt,
        UUID createdBy
) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getInvoiceId(),
                p.getMethod(),
                p.getAmount(),
                p.getReference(),
                p.getPaidAt(),
                p.getCreatedBy()
        );
    }
}
