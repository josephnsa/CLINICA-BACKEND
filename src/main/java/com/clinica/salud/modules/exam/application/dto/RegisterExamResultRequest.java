package com.clinica.salud.modules.exam.application.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterExamResultRequest(
        @NotBlank String resultText
) {
}

