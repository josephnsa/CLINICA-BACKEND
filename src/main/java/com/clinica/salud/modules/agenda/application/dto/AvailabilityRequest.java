package com.clinica.salud.modules.agenda.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AvailabilityRequest(
        UUID doctorId,
        UUID sedeId,
        LocalDate date
) {}
