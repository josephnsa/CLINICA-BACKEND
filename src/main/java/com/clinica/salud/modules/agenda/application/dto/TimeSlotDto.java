package com.clinica.salud.modules.agenda.application.dto;

import java.time.LocalDateTime;

public record TimeSlotDto(
        LocalDateTime start,
        LocalDateTime end,
        boolean available
) {}
