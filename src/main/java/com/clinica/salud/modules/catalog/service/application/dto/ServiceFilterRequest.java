package com.clinica.salud.modules.catalog.service.application.dto;

import java.util.UUID;

public record ServiceFilterRequest(
        UUID specialtyId,
        boolean activeOnly
) {}

