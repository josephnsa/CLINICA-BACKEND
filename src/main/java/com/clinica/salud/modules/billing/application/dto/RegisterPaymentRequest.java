package com.clinica.salud.modules.billing.application.dto;

import com.clinica.salud.modules.billing.domain.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record RegisterPaymentRequest(
        @NotNull(message = "invoiceId es obligatorio") UUID invoiceId,
        @NotNull(message = "method es obligatorio") PaymentMethod method,
        @NotNull(message = "amount es obligatorio") @Positive(message = "amount debe ser positivo") BigDecimal amount,
        String reference
) {}
