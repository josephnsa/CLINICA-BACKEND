package com.clinica.salud.modules.dashboard.application.usecase;

import com.clinica.salud.modules.dashboard.application.dto.HeaderUserResponse;
import com.clinica.salud.modules.dashboard.application.dto.HeaderUserResponse.SedeOption;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetHeaderUserUseCase {

    private final JdbcTemplate jdbc;

    private static final Map<String, String> ROLE_LABELS = Map.of(
            "ADMIN",        "Administrador",
            "MEDICO",       "Médico",
            "ENFERMERA",    "Enfermera",
            "RECEPCIONISTA","Recepcionista",
            "FARMACIA",     "Farmacia",
            "LABORATORIO",  "Laboratorio"
    );

    @Transactional(readOnly = true)
    public HeaderUserResponse execute(UUID userId) {
        // Datos del usuario + sede principal
        Map<String, Object> user = jdbc.queryForMap("""
                SELECT u.id::text  AS user_id,
                       u.full_name,
                       r.code      AS role_code,
                       s.id::text  AS sede_id,
                       s.name      AS sede_name
                FROM users u
                JOIN roles r ON u.role_id = r.id
                JOIN sedes s ON u.sede_id = s.id
                WHERE u.id = ?
                  AND u.is_active = true
                """, userId);

        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User", userId.toString());
        }

        String fullName  = (String) user.get("full_name");
        String roleCode  = (String) user.get("role_code");
        UUID   sedeId    = UUID.fromString((String) user.get("sede_id"));
        String sedeName  = (String) user.get("sede_name");

        // Sedes disponibles (ADMIN puede ver todas; otros solo la suya)
        List<SedeOption> availableSedes;
        if ("ADMIN".equals(roleCode)) {
            availableSedes = jdbc.queryForList("SELECT id::text AS id, name FROM sedes ORDER BY name")
                    .stream()
                    .map(r -> new SedeOption(
                            UUID.fromString((String) r.get("id")),
                            (String) r.get("name")))
                    .collect(Collectors.toList());
        } else {
            availableSedes = List.of(new SedeOption(sedeId, sedeName));
        }

        return new HeaderUserResponse(
                userId,
                fullName,
                buildInitials(fullName),
                roleCode,
                ROLE_LABELS.getOrDefault(roleCode, roleCode),
                sedeId,
                sedeName,
                availableSedes
        );
    }

    private String buildInitials(String fullName) {
        if (fullName == null || fullName.isBlank()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }
}
