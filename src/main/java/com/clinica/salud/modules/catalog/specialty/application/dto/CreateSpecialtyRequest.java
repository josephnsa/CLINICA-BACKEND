package com.clinica.salud.modules.catalog.specialty.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSpecialtyRequest(
        @NotBlank(message = "code es obligatorio") String code,
        @NotBlank(message = "name es obligatorio") String name
) {}

