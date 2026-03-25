package com.clinica.salud.modules.agenda.application.dto;

import com.clinica.salud.modules.agenda.domain.model.TriageLevel;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record RegisterTriageRequest(
        @NotNull UUID appointmentId,
        @NotNull UUID patientId,
        String bloodPressure,
        Integer heartRate,
        Integer respiratoryRate,
        BigDecimal temperature,
        Integer oxygenSaturation,
        BigDecimal weight,
        BigDecimal height,
        TriageLevel triageLevel,
        String notes
) {}
