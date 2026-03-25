package com.clinica.salud.modules.auth.application.dto;

import java.util.List;
import java.util.UUID;

public record MeResponse(
        UUID id,
        String email,
        String fullName,
        String role,
        List<String> permissions
) {}
