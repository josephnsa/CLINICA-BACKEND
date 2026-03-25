package com.clinica.salud.modules.agenda.application.dto;

import com.clinica.salud.modules.agenda.domain.model.TriageLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TriageResponse(
        UUID id,
        UUID appointmentId,
        UUID patientId,
        UUID recordedBy,
        String bloodPressure,
        Integer heartRate,
        Integer respiratoryRate,
        BigDecimal temperature,
        Integer oxygenSaturation,
        BigDecimal weight,
        BigDecimal height,
        TriageLevel triageLevel,
        String notes,
        LocalDateTime recordedAt,
        boolean critical,
        Double bmi
) {}
