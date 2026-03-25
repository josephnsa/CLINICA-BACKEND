package com.clinica.salud.modules.catalog.sede.infrastructure.web;

import com.clinica.salud.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalog/sedes")
@RequiredArgsConstructor
public class SedeController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    @PreAuthorize("hasAuthority('CATALOGO_READ')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> list() {
        String sqlSedes = """
            SELECT id,
                   code,
                   name,
                   address,
                   is_active AS "isActive"
            FROM sedes
            ORDER BY name
            """;

        List<Map<String, Object>> sedesRaw = jdbcTemplate.queryForList(sqlSedes);

        // Obtener usuarios por sede (email, fullName, rol)
        String sqlUsers = """
            SELECT us.sede_id        AS sedeId,
                   u.id              AS userId,
                   u.email           AS email,
                   u.full_name       AS fullName,
                   r.code            AS roleCode,
                   r.name            AS roleName
            FROM user_sedes us
            JOIN users u   ON us.user_id = u.id
            LEFT JOIN roles r ON u.role_id = r.id
            """;

        List<Map<String, Object>> usersRaw = jdbcTemplate.queryForList(sqlUsers);

        // Agrupar usuarios por sedeId
        Map<Object, List<Map<String, Object>>> usersBySede = new HashMap<>();
        for (Map<String, Object> row : usersRaw) {
            Object sedeId = row.get("sedeid");
            Map<String, Object> u = new HashMap<>();
            u.put("id", row.get("userid"));
            u.put("email", row.get("email"));
            u.put("fullName", row.get("fullname"));
            u.put("roleCode", row.get("rolecode"));
            u.put("roleName", row.get("rolename"));
            usersBySede.computeIfAbsent(sedeId, k -> new ArrayList<>()).add(u);
        }

        // Adjuntar lista de usuarios a cada sede
        List<Map<String, Object>> sedes = new ArrayList<>();
        for (Map<String, Object> s : sedesRaw) {
            Map<String, Object> copy = new HashMap<>(s);
            Object sedeId = s.get("id");
            List<Map<String, Object>> users = usersBySede.getOrDefault(sedeId, List.of());
            copy.put("users", users);
            sedes.add(copy);
        }

        return ResponseEntity.ok(ApiResponse.ok(sedes));
    }
}


