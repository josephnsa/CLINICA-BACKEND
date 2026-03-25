package com.clinica.salud.modules.exam.application.dto;

import com.clinica.salud.modules.exam.domain.model.ExamOrderStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ExamOrderResponse(
        UUID id,
        UUID patientId,
        UUID doctorId,
        UUID appointmentId,
        ExamOrderStatus status,
        String notes,
        OffsetDateTime createdAt,
        UUID createdBy,
        List<ExamOrderItemResponse> items
) {
}

