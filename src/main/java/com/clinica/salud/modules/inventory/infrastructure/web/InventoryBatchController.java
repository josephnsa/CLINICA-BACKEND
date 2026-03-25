package com.clinica.salud.modules.inventory.infrastructure.web;

import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory/batches")
@RequiredArgsConstructor
public class InventoryBatchController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTARIO_READ')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> list(
            @RequestParam(required = false) UUID itemId) {

        String sql = itemId != null
                ? "SELECT b.*, i.name AS itemName FROM inventory_batches b LEFT JOIN inventory_items i ON b.item_id = i.id WHERE b.item_id = '" + itemId + "' ORDER BY b.expiry_date"
                : "SELECT b.*, i.name AS itemName FROM inventory_batches b LEFT JOIN inventory_items i ON b.item_id = i.id ORDER BY b.expiry_date";

        return ResponseEntity.ok(ApiResponse.ok(jdbcTemplate.queryForList(sql)));
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAuthority('INVENTARIO_READ')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getExpiring(
            @RequestParam(defaultValue = "30") int daysAhead) {

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
            SELECT b.id,
                   b.item_id       AS "itemId",
                   i.name          AS "itemName",
                   b.batch_number  AS "batchNumber",
                   b.quantity,
                   b.expiry_date   AS "expiryDate",
                   b.received_at   AS "receivedAt",
                   b.notes
            FROM inventory_batches b
            LEFT JOIN inventory_items i ON b.item_id = i.id
            WHERE b.expiry_date <= CURRENT_DATE + ?::int
              AND b.quantity > 0
            ORDER BY b.expiry_date
            """, daysAhead);

        return ResponseEntity.ok(ApiResponse.ok(rows));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INVENTARIO_WRITE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(
            @Valid @RequestBody RegisterBatchRequest request) {

        UUID id = UUID.randomUUID();
        jdbcTemplate.update("""
            INSERT INTO inventory_batches (id, item_id, batch_number, quantity, expiry_date, notes)
            VALUES (?, ?, ?, ?, ?, ?)
            """, id, request.itemId(), request.batchNumber(), request.quantity(),
                request.expiryDate(), request.notes());

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM inventory_batches WHERE id = ?", id);
        return ResponseEntity.ok(ApiResponse.ok(rows.get(0)));
    }

    public record RegisterBatchRequest(
            @NotNull UUID itemId,
            @NotBlank String batchNumber,
            @NotNull @Min(1) Integer quantity,
            @NotNull LocalDate expiryDate,
            String notes
    ) {}
}
