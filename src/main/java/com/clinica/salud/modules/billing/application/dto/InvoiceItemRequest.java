package com.clinica.salud.modules.billing.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InvoiceItemRequest(
        @NotNull(message = "serviceId es obligatorio") UUID serviceId,
        @Min(1) int quantity
) {}
