package com.clinica.salud.modules.prescription.application.dto;

import com.clinica.salud.modules.prescription.domain.model.PrescriptionStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PrescriptionResponse(
        UUID id,
        UUID patientId,
        UUID doctorId,
        UUID appointmentId,
        UUID diagnosisId,
        String notes,
        PrescriptionStatus status,
        OffsetDateTime createdAt,
        UUID createdBy,
        List<PrescriptionItemResponse> items
) {
}

