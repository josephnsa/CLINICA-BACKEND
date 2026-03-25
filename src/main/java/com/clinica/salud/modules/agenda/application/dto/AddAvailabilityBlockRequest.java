package com.clinica.salud.modules.agenda.application.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record AddAvailabilityBlockRequest(
        @NotNull UUID doctorId,
        @NotNull UUID sedeId,
        @NotNull LocalDateTime startDt,
        @NotNull LocalDateTime endDt,
        String reason
) {}
