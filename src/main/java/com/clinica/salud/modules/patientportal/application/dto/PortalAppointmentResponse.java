package com.clinica.salud.modules.patientportal.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PortalAppointmentResponse {

    private UUID id;
    private String doctorName;
    private String specialtyName;
    private String serviceName;
    private String sedeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String notes;
}
