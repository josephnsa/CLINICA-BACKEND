package com.clinica.salud.modules.agenda.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RescheduleRequest {

    @NotNull(message = "La nueva fecha/hora de inicio es obligatoria")
    private LocalDateTime newStart;

    @NotNull(message = "La nueva fecha/hora de fin es obligatoria")
    private LocalDateTime newEnd;

    private String reason;
}
