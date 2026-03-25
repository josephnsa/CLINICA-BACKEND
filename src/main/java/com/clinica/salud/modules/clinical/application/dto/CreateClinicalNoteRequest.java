package com.clinica.salud.modules.clinical.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateClinicalNoteRequest(
        @NotNull(message = "appointmentId es obligatorio") UUID appointmentId,
        @NotBlank(message = "reason es obligatorio") String reason,
        String physicalExam,
        @NotBlank(message = "diagnosisCode es obligatorio") String diagnosisCode,
        String diagnosisDescription,
        @NotBlank(message = "treatmentPlan es obligatorio") String treatmentPlan
) {}
