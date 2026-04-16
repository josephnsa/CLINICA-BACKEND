package com.clinica.salud.modules.dashboard.application.dto;

import java.util.List;
import java.util.UUID;

public record HeaderUserResponse(
        UUID   userId,
        String fullName,
        String initials,     // primeras letras del nombre para avatar
        String role,
        String roleLabel,    // etiqueta legible: "Administrador", "Médico", etc.
        UUID   sedeId,
        String sedeName,
        List<SedeOption> availableSedes  // sedes a las que tiene acceso (para selector)
) {
    public record SedeOption(UUID id, String name) {}
}
