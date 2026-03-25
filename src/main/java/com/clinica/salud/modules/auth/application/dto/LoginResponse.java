package com.clinica.salud.modules.auth.application.dto;

import java.util.List;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String fullName,
        String role,
        List<String> permissions
) {}
