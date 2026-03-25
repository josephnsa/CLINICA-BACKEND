package com.clinica.salud.modules.agenda.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.UUID;

public record SetAvailabilityRuleRequest(
        @NotNull UUID doctorId,
        @NotNull UUID sedeId,
        @NotNull @Min(1) @Max(7) Integer dayOfWeek,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime
) {}
