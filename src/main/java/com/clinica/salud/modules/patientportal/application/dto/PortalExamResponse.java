package com.clinica.salud.modules.patientportal.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PortalExamResponse {

    private UUID orderId;
    private String serviceName;
    private String orderStatus;
    private String resultText;
    private LocalDateTime resultAt;
    private LocalDateTime createdAt;
}
