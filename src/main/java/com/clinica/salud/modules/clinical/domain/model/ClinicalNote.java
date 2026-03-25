package com.clinica.salud.modules.clinical.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClinicalNote {

    private UUID id;
    private UUID appointmentId;
    private UUID patientId;
    private UUID doctorId;
    private String reason;
    private String physicalExam;
    private String diagnosisCode;
    private String diagnosisDesc;
    private String treatmentPlan;
    private LocalDateTime createdAt;
}
