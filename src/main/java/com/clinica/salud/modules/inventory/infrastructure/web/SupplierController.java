package com.clinica.salud.modules.inventory.infrastructure.web;

import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTARIO_READ')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> list(
            @RequestParam(required = false) Boolean active) {
        String where = active != null ? " WHERE is_active = " + active : "";
        return ResponseEntity.ok(ApiResponse.ok(
                jdbcTemplate.queryForList("SELECT * FROM suppliers" + where + " ORDER BY name")));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTARIO_READ')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable UUID id) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM suppliers WHERE id = ?", id);
        if (rows.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ApiResponse.ok(rows.get(0)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INVENTARIO_WRITE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(
            @Valid @RequestBody CreateSupplierRequest request) {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update("""
            INSERT INTO suppliers (id, name, ruc, contact, phone, email, address, is_active)
            VALUES (?, ?, ?, ?, ?, ?, ?, true)
            """, id, request.name(), request.ruc(), request.contact(),
                request.phone(), request.email(), request.address());

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM suppliers WHERE id = ?", id);
        return ResponseEntity.ok(ApiResponse.ok(rows.get(0)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTARIO_WRITE')")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateSupplierRequest request) {
        int updated = jdbcTemplate.update("""
            UPDATE suppliers SET name=?, ruc=?, contact=?, phone=?, email=?, address=?
            WHERE id=?
            """, request.name(), request.ruc(), request.contact(),
                request.phone(), request.email(), request.address(), id);
        if (updated == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('INVENTARIO_WRITE')")
    public ResponseEntity<ApiResponse<Void>> toggle(@PathVariable UUID id) {
        int updated = jdbcTemplate.update(
                "UPDATE suppliers SET is_active = NOT is_active WHERE id = ?", id);
        if (updated == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    public record CreateSupplierRequest(
            @NotBlank String name,
            String ruc,
            String contact,
            String phone,
            String email,
            String address
    ) {}
}
