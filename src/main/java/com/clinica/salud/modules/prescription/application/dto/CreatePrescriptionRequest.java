package com.clinica.salud.modules.prescription.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreatePrescriptionRequest(
        @NotNull UUID patientId,
        @NotNull UUID doctorId,
        @NotNull UUID appointmentId,
        UUID diagnosisId,
        String notes,
        @NotEmpty @Valid List<PrescriptionItemRequest> items
) {
}

