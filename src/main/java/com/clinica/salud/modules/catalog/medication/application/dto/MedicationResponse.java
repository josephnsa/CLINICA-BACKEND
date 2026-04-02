package com.clinica.salud.modules.catalog.medication.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record MedicationResponse(
        UUID id,
        String code,
        String genericName,
        @JsonProperty("tradeName") String commercialName,
        String presentation,
        String unit,
        @JsonProperty("active") boolean isActive
) {}

