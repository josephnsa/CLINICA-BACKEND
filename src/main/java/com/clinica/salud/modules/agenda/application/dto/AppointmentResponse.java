package com.clinica.salud.modules.agenda.application.dto;

import com.clinica.salud.modules.agenda.domain.model.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID patientId,
        UUID doctorId,
        UUID serviceId,
        UUID sedeId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status,
        String notes,
        String cancellationReason,
        UUID createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String patientFullName,
        String doctorFullName,
        String serviceName
) {}
