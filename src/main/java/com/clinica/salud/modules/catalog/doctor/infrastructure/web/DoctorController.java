package com.clinica.salud.modules.catalog.doctor.infrastructure.web;

import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    @PreAuthorize("hasAuthority('CATALOGO_READ')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> list(
            @RequestParam(required = false) UUID specialtyId,
            @RequestParam(required = false) Boolean active) {

        StringBuilder sql = new StringBuilder("""
            SELECT d.id,
                   d.user_id        AS "userId",
                   d.license_number AS "licenseNumber",
                   d.specialty_id   AS "specialtyId",
                   s.name           AS "specialtyName",
                   d.is_active      AS "isActive",
                   d.created_at     AS "createdAt",
                   u.full_name      AS "fullName",
                   u.email          AS "email"
            FROM doctors d
            LEFT JOIN specialties s ON d.specialty_id = s.id
            LEFT JOIN users u ON d.user_id = u.id
            WHERE 1=1
            """);

        if (specialtyId != null) sql.append(" AND d.specialty_id = '").append(specialtyId).append("'");
        if (active != null) sql.append(" AND d.is_active = ").append(active);
        sql.append(" ORDER BY u.full_name");

        return ResponseEntity.ok(ApiResponse.ok(jdbcTemplate.queryForList(sql.toString())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CATALOGO_READ')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable UUID id) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
            SELECT d.id,
                   d.user_id        AS "userId",
                   d.license_number AS "licenseNumber",
                   d.specialty_id   AS "specialtyId",
                   s.name           AS "specialtyName",
                   d.is_active      AS "isActive",
                   d.created_at     AS "createdAt",
                   u.full_name      AS "fullName",
                   u.email          AS "email"
            FROM doctors d
            LEFT JOIN specialties s ON d.specialty_id = s.id
            LEFT JOIN users u ON d.user_id = u.id
            WHERE d.id = ?
            """, id);

        if (rows.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.ok(rows.get(0)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(
            @Valid @RequestBody CreateDoctorRequest request) {

        UUID id = UUID.randomUUID();
        jdbcTemplate.update("""
            INSERT INTO doctors (id, user_id, license_number, specialty_id, is_active)
            VALUES (?, ?, ?, ?, true)
            """, id, request.userId(), request.licenseNumber(), request.specialtyId());

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM doctors WHERE id = ?", id);
        return ResponseEntity.ok(ApiResponse.ok(rows.get(0)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDoctorRequest request) {

        int updated = jdbcTemplate.update("""
            UPDATE doctors SET license_number = ?, specialty_id = ?
            WHERE id = ?
            """, request.licenseNumber(), request.specialtyId(), id);

        if (updated == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('CATALOGO_WRITE')")
    public ResponseEntity<ApiResponse<Void>> toggle(@PathVariable UUID id) {
        int updated = jdbcTemplate.update("""
            UPDATE doctors SET is_active = NOT is_active WHERE id = ?
            """, id);
        if (updated == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ── Inner request records ─────────────────────────────────────

    public record CreateDoctorRequest(
            @NotNull UUID userId,
            @NotBlank String licenseNumber,
            UUID specialtyId
    ) {}

    public record UpdateDoctorRequest(
            @NotBlank String licenseNumber,
            UUID specialtyId
    ) {}
}
