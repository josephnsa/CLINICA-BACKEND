package com.clinica.salud.modules.exam.application.dto;

import com.clinica.salud.modules.exam.domain.model.ExamOrderStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ExamOrderItemResponse(
        UUID id,
        UUID serviceId,
        ExamOrderStatus status,
        String resultText,
        OffsetDateTime resultAt,
        UUID resultBy
) {
}

