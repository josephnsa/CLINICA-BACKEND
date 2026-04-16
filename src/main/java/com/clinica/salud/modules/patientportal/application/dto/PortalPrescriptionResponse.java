package com.clinica.salud.modules.patientportal.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PortalPrescriptionResponse {

    private UUID id;
    private String diagnosisCode;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private List<PrescriptionItemDetail> medications;

    @Data
    @Builder
    public static class PrescriptionItemDetail {
        private String medicationName;
        private String dose;
        private String frequency;
        private String duration;
        private String instructions;
        private boolean dispensed;
    }
}
