package com.clinica.salud.modules.catalog.specialty.application.dto;

import java.util.UUID;

public record SpecialtyResponse(
        UUID id,
        String code,
        String name,
        boolean isActive
) {}

