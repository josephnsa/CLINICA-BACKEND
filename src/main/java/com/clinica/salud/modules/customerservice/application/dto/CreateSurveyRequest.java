package com.clinica.salud.modules.customerservice.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateSurveyRequest {

    @NotNull(message = "El paciente es obligatorio")
    private UUID patientId;

    private UUID appointmentId;

    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private int score;

    private String comment;
}
