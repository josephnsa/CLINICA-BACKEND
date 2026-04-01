package com.clinica.salud.modules.catalog.service.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateServiceRequest(
        @NotBlank(message = "code es obligatorio") String code,
        @NotBlank(message = "name es obligatorio") String name,
        UUID specialtyId,
        @NotNull @Positive(message = "durationMin debe ser positivo") Integer durationMin,
        @NotNull @PositiveOrZero(message = "price no puede ser negativo") BigDecimal price,
        boolean isActive
) {
}
