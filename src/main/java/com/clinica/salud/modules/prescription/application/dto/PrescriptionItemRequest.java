package com.clinica.salud.modules.prescription.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PrescriptionItemRequest(
        @NotNull UUID medicationId,
        @NotBlank String dose,
        @NotBlank String frequency,
        String duration,
        String route,
        String instructions,
        @Min(0) int quantity
) {
}

