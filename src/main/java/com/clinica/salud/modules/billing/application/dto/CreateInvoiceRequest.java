package com.clinica.salud.modules.billing.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateInvoiceRequest(
        @NotNull(message = "patientId es obligatorio") UUID patientId,
        @NotNull(message = "sedeId es obligatorio") UUID sedeId,
        UUID appointmentId,
        @NotEmpty(message = "items no puede estar vacío") @Valid List<InvoiceItemRequest> items
) {}
