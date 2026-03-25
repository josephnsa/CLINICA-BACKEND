package com.clinica.salud.modules.catalog.service.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceResponse(
        UUID id,
        String code,
        String name,
        UUID specialtyId,
        String specialtyName,
        int durationMin,
        BigDecimal price,
        boolean isActive
) {}

