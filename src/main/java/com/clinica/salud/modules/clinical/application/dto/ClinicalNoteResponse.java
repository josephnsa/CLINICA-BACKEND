package com.clinica.salud.modules.clinical.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Nota clínica con datos expandidos de médico y paciente.
 */
public record ClinicalNoteResponse(
        UUID id,
        UUID appointmentId,
        UUID patientId,
        UUID doctorId,
        String reason,
        String physicalExam,
        String diagnosisCode,
        String diagnosisDesc,
        String treatmentPlan,
        LocalDateTime createdAt,
        String patientFullName,
        String doctorFullName
) {}
