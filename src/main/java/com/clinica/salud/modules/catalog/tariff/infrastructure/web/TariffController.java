package com.clinica.salud.modules.catalog.tariff.infrastructure.web;

import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/tariffs")
@RequiredArgsConstructor
public class TariffController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    @PreAuthorize("hasAuthority('CATALOGO_READ')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> list(
            @RequestParam(required = false) UUID sedeId,
            @RequestParam(required = false) UUID serviceId) {

        StringBuilder sql = new StringBuilder("""
            SELECT t.id,
                   t.service_id  AS "serviceId",
                   sv.name       AS "serviceName",
                   t.sede_id     AS "sedeId",
                   sd.name       AS "sedeName",
                   t.name,
                   t.price,
                   t.is_active   AS "isActive"
            FROM tariffs t
            LEFT JOIN services sv ON t.service_id = sv.id
            LEFT JOIN sedes sd ON t.sede_id = sd.id
            WHERE 1=1
            """);

        if (sedeId != null) sql.append(" AND t.sede_id = '").append(sedeId).append("'");
        if (serviceId != null) sql.append(" AND t.service_id = '").append(serviceId).append("'");
        sql.append(" ORDER BY sv.name");

        return ResponseEntity.ok(ApiResponse.ok(jdbcTemplate.queryForList(sql.toString())));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(
            @Valid @RequestBody CreateTariffRequest request) {

        UUID id = UUID.randomUUID();
        jdbcTemplate.update("""
            INSERT INTO tariffs (id, service_id, sede_id, name, price, is_active)
            VALUES (?, ?, ?, ?, ?, true)
            """, id, request.serviceId(), request.sedeId(), request.name(), request.price());

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM tariffs WHERE id = ?", id);
        return ResponseEntity.ok(ApiResponse.ok(rows.get(0)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTariffRequest request) {

        int updated = jdbcTemplate.update("""
            UPDATE tariffs SET name = ?, price = ? WHERE id = ?
            """, request.name(), request.price(), id);

        if (updated == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<Void>> toggle(@PathVariable UUID id) {
        int updated = jdbcTemplate.update("""
            UPDATE tariffs SET is_active = NOT is_active WHERE id = ?
            """, id);
        if (updated == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ── Inner request records ─────────────────────────────────────

    public record CreateTariffRequest(
            @NotNull UUID serviceId,
            @NotNull UUID sedeId,
            @NotBlank String name,
            @NotNull @Positive BigDecimal price
    ) {}

    public record UpdateTariffRequest(
            @NotBlank String name,
            @NotNull @Positive BigDecimal price
    ) {}
}
