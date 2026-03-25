package com.clinica.salud.modules.patients.domain.model;

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
public class ClinicalProfile {

    private UUID id;
    private UUID patientId;
    private String allergies;
    private String personalHistory;
    private String familyHistory;
    private String surgicalHistory;
    private String currentMeds;
    private LocalDateTime updatedAt;
}
