package com.clinica.salud.modules.agenda.application.dto;

import java.time.LocalTime;
import java.util.UUID;

public record AvailabilityRuleResponse(
        UUID id,
        UUID doctorId,
        UUID sedeId,
        int dayOfWeek,
        String dayName,
        LocalTime startTime,
        LocalTime endTime,
        boolean active
) {}
