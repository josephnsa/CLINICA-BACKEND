package com.clinica.salud.modules.prescription.application.dto;

import java.util.UUID;

public record PrescriptionItemResponse(
        UUID id,
        UUID medicationId,
        String dose,
        String frequency,
        String duration,
        String route,
        String instructions,
        int quantity,
        boolean dispensed
) {
}

