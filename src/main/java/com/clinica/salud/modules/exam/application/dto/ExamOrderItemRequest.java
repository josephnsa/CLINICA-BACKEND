package com.clinica.salud.modules.exam.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ExamOrderItemRequest(
        @NotNull UUID serviceId
) {
}

