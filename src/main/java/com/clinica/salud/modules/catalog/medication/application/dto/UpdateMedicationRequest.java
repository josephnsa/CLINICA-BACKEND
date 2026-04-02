package com.clinica.salud.modules.catalog.medication.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateMedicationRequest(
        @NotBlank(message = "code es obligatorio") String code,
        @NotBlank(message = "genericName es obligatorio") String genericName,
        String commercialName,
        String presentation,
        String unit,
        boolean active
) {}
