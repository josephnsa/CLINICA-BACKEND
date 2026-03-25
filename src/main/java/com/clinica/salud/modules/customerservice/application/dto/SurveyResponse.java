package com.clinica.salud.modules.customerservice.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SurveyResponse {

    private UUID id;
    private UUID patientId;
    private UUID appointmentId;
    private int score;
    private String scoreLabel;
    private String comment;
    private boolean positive;
    private LocalDateTime createdAt;
}
