package com.clinica.salud.modules.agenda.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAppointmentRequest(
        @NotNull(message = "patientId es obligatorio") UUID patientId,
        @NotNull(message = "doctorId es obligatorio") UUID doctorId,
        @NotNull(message = "serviceId es obligatorio") UUID serviceId,
        @NotNull(message = "sedeId es obligatorio") UUID sedeId,
        @NotNull(message = "startTime es obligatorio") @Future(message = "startTime debe ser futura") LocalDateTime startTime,
        @NotNull(message = "endTime es obligatorio") LocalDateTime endTime
) {}
