package com.clinica.salud.modules.catalog.sede.infrastructure.web;

import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @PostMapping
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(
            @Valid @RequestBody CreateSedeRequest request) {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update("""
            INSERT INTO sedes (id, code, name, address, is_active)
            VALUES (?, ?, ?, ?, true)
            """, id, request.code(), request.name(), request.address());

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM sedes WHERE id = ?", id);
        return ResponseEntity.ok(ApiResponse.ok(rows.get(0)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSedeRequest request) {
        int updated = jdbcTemplate.update("""
            UPDATE sedes SET name = ?, address = ? WHERE id = ?
            """, request.name(), request.address(), id);
        if (updated == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<Void>> toggle(@PathVariable UUID id) {
        int updated = jdbcTemplate.update("""
            UPDATE sedes SET is_active = NOT is_active WHERE id = ?
            """, id);
        if (updated == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    public record CreateSedeRequest(@NotBlank String code, @NotBlank String name, String address) {}
    public record UpdateSedeRequest(@NotBlank String name, String address) {}
}
