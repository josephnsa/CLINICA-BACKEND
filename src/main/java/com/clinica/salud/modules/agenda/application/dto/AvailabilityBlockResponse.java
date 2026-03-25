package com.clinica.salud.modules.agenda.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AvailabilityBlockResponse(
        UUID id,
        UUID doctorId,
        UUID sedeId,
        LocalDateTime startDt,
        LocalDateTime endDt,
        String reason
) {}
