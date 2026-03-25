package com.clinica.salud.modules.prescription.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Prescription {

    private UUID id;
    private UUID patientId;
    private UUID doctorId;
    private UUID appointmentId;
    private UUID diagnosisId;
    private String notes;
    private PrescriptionStatus status;
    private OffsetDateTime createdAt;
    private UUID createdBy;
    private List<PrescriptionItem> items;
}

