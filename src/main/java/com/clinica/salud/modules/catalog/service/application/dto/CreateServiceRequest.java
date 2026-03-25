package com.clinica.salud.modules.catalog.service.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateServiceRequest(
        @NotBlank(message = "code es obligatorio") String code,
        @NotBlank(message = "name es obligatorio") String name,
        @NotNull(message = "specialtyId es obligatorio") UUID specialtyId,
        @Positive(message = "durationMin debe ser positivo") int durationMin,
        @PositiveOrZero(message = "price no puede ser negativo") BigDecimal price
) {}

