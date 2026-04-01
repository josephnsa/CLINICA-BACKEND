package com.clinica.salud.modules.security.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserCreateRequest(
        @NotBlank @Email @Size(max = 200) String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Size(max = 200) String fullName,
        @NotNull UUID roleId,
        Boolean active
) {
}
