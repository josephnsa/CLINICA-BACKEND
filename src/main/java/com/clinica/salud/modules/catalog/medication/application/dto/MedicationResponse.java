package com.clinica.salud.modules.catalog.medication.application.dto;

import java.util.UUID;

public record MedicationResponse(
        UUID id,
        String code,
        String genericName,
        String commercialName,
        String presentation,
        String unit,
        boolean isActive
) {}

