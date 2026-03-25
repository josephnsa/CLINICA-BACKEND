package com.clinica.salud.modules.inventory.infrastructure.web;

import com.clinica.salud.shared.exception.UnauthorizedException;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTARIO_READ')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> list(
            @RequestParam(required = false) UUID sedeId,
            @RequestParam(required = false) String status) {

        StringBuilder sql = new StringBuilder("""
            SELECT po.id,
                   po.supplier_id  AS "supplierId",
                   s.name          AS "supplierName",
                   po.sede_id      AS "sedeId",
                   sd.name         AS "sedeName",
                   po.status,
                   po.total,
                   po.notes,
                   po.created_at   AS "createdAt",
                   po.received_at  AS "receivedAt"
            FROM purchase_orders po
            LEFT JOIN suppliers s  ON po.supplier_id = s.id
            LEFT JOIN sedes sd     ON po.sede_id = sd.id
            WHERE 1=1
            """);
        if (sedeId != null) sql.append(" AND po.sede_id = '").append(sedeId).append("'");
        if (status != null) sql.append(" AND po.status = '").append(status).append("'");
        sql.append(" ORDER BY po.created_at DESC");

        return ResponseEntity.ok(ApiResponse.ok(jdbcTemplate.queryForList(sql.toString())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTARIO_READ')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable UUID id) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
            SELECT po.*, s.name AS "supplierName"
            FROM purchase_orders po
            LEFT JOIN suppliers s ON po.supplier_id = s.id
            WHERE po.id = ?
            """, id);
        if (rows.isEmpty()) return ResponseEntity.notFound().build();

        Map<String, Object> order = rows.get(0);
        List<Map<String, Object>> items = jdbcTemplate.queryForList("""
            SELECT poi.*, m.generic_name AS "medicationName"
            FROM purchase_order_items poi
            LEFT JOIN medications m ON poi.medication_id = m.id
            WHERE poi.purchase_order_id = ?
            """, id);
        order.put("items", items);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INVENTARIO_WRITE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(
            @Valid @RequestBody CreatePurchaseOrderRequest request) {
        UUID createdBy = getCurrentUserId();
        UUID orderId = UUID.randomUUID();

        BigDecimal total = request.items().stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        jdbcTemplate.update("""
            INSERT INTO purchase_orders (id, supplier_id, sede_id, status, total, notes, created_by)
            VALUES (?, ?, ?, 'PENDING', ?, ?, ?)
            """, orderId, request.supplierId(), request.sedeId(), total, request.notes(), createdBy);

        for (OrderItemRequest item : request.items()) {
            jdbcTemplate.update("""
                INSERT INTO purchase_order_items (id, purchase_order_id, medication_id, description, quantity, unit_price, total)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, UUID.randomUUID(), orderId, item.medicationId(), item.description(),
                    item.quantity(), item.unitPrice(), item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())));
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM purchase_orders WHERE id = ?", orderId);
        return ResponseEntity.ok(ApiResponse.ok(rows.get(0)));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('INVENTARIO_WRITE')")
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable UUID id) {
        int updated = jdbcTemplate.update(
                "UPDATE purchase_orders SET status = 'APPROVED' WHERE id = ? AND status = 'PENDING'", id);
        if (updated == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/{id}/receive")
    @PreAuthorize("hasAuthority('INVENTARIO_WRITE')")
    public ResponseEntity<ApiResponse<Void>> receive(@PathVariable UUID id) {
        int updated = jdbcTemplate.update("""
            UPDATE purchase_orders SET status = 'RECEIVED', received_at = ?
            WHERE id = ? AND status = 'APPROVED'
            """, LocalDateTime.now(), id);
        if (updated == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('INVENTARIO_WRITE')")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable UUID id) {
        int updated = jdbcTemplate.update("""
            UPDATE purchase_orders SET status = 'CANCELLED'
            WHERE id = ? AND status IN ('PENDING','APPROVED')
            """, id);
        if (updated == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private static UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof String)) {
            throw new UnauthorizedException("Usuario no autenticado");
        }
        try {
            return UUID.fromString(auth.getPrincipal().toString());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Identidad de usuario inválida");
        }
    }

    public record CreatePurchaseOrderRequest(
            @NotNull UUID supplierId,
            @NotNull UUID sedeId,
            String notes,
            @NotNull List<OrderItemRequest> items
    ) {}

    public record OrderItemRequest(
            UUID medicationId,
            @NotBlank String description,
            @NotNull @Min(1) Integer quantity,
            @NotNull @Positive BigDecimal unitPrice
    ) {}
}
