package com.clinica.salud.modules.security.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserUpdateRequest(
        @NotBlank String fullName,
        @NotNull UUID roleId,
        boolean active
) {
}
