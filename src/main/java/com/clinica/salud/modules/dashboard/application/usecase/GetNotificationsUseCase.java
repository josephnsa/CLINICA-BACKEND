package com.clinica.salud.modules.dashboard.application.usecase;

import com.clinica.salud.modules.dashboard.application.dto.NotificationSummaryResponse;
import com.clinica.salud.modules.dashboard.application.dto.NotificationSummaryResponse.NotificationItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetNotificationsUseCase {

    private final JdbcTemplate jdbc;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional(readOnly = true)
    public NotificationSummaryResponse execute(UUID sedeId) {

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay   = today.plusDays(1).atStartOfDay();

        // 1. Citas de hoy (PENDING o CONFIRMED)
        int appointmentsToday = count("""
                SELECT COUNT(*) FROM appointments
                WHERE sede_id  = ?
                  AND start_time >= ?
                  AND start_time <  ?
                  AND status IN ('PENDING','CONFIRMED')
                """, sedeId, startOfDay, endOfDay);

        // 2. Resultados de examen listos sin notificar (COMPLETED)
        int pendingExamResults = count("""
                SELECT COUNT(*) FROM exam_order_items eoi
                JOIN exam_orders eo ON eoi.order_id = eo.id
                WHERE eo.sede_id  = ?
                  AND eoi.status  = 'COMPLETED'
                  AND eoi.result_at >= ?
                """, sedeId, today.minusDays(7).atStartOfDay());

        // 3. Facturas pendientes de pago (PENDING) de los últimos 30 días
        int pendingInvoices = count("""
                SELECT COUNT(*) FROM invoices
                WHERE sede_id    = ?
                  AND status     = 'PENDING'
                  AND created_at >= ?
                """, sedeId, today.minusDays(30).atStartOfDay());

        // 4. Productos con stock bajo (stock_quantity <= reorder_point)
        int lowStockItems = count("""
                SELECT COUNT(*) FROM inventory_items
                WHERE sede_id        = ?
                  AND stock_quantity <= reorder_point
                  AND is_active      = true
                """, sedeId);

        // 5. Construir lista de notificaciones recientes
        List<NotificationItem> items = new ArrayList<>();

        // Citas de hoy
        if (appointmentsToday > 0) {
            List<Map<String, Object>> appts = jdbc.queryForList("""
                    SELECT a.id::text AS id,
                           p.first_name || ' ' || p.last_name AS patient,
                           a.start_time
                    FROM appointments a
                    JOIN patients p ON a.patient_id = p.id
                    WHERE a.sede_id   = ?
                      AND a.start_time >= ?
                      AND a.start_time <  ?
                      AND a.status IN ('PENDING','CONFIRMED')
                    ORDER BY a.start_time
                    LIMIT 5
                    """, sedeId, startOfDay, endOfDay);

            for (Map<String, Object> row : appts) {
                LocalDateTime dt = (LocalDateTime) row.get("start_time");
                String hora = dt != null ? dt.format(DateTimeFormatter.ofPattern("HH:mm")) : "";
                items.add(new NotificationItem(
                        "APPOINTMENT",
                        "Cita programada — " + hora,
                        "Paciente: " + row.get("patient"),
                        "info",
                        dt != null ? dt.format(FMT) : "",
                        (String) row.get("id")
                ));
            }
        }

        // Resultados de exámenes listos
        if (pendingExamResults > 0) {
            List<Map<String, Object>> results = jdbc.queryForList("""
                    SELECT eoi.id::text AS id,
                           sv.name     AS exam_name,
                           p.first_name || ' ' || p.last_name AS patient,
                           eoi.result_at
                    FROM exam_order_items eoi
                    JOIN exam_orders eo ON eoi.order_id  = eo.id
                    JOIN services   sv ON eoi.service_id = sv.id
                    JOIN patients    p ON eo.patient_id  = p.id
                    WHERE eo.sede_id   = ?
                      AND eoi.status   = 'COMPLETED'
                      AND eoi.result_at >= ?
                    ORDER BY eoi.result_at DESC
                    LIMIT 5
                    """, sedeId, today.minusDays(7).atStartOfDay());

            for (Map<String, Object> row : results) {
                LocalDateTime dt = (LocalDateTime) row.get("result_at");
                items.add(new NotificationItem(
                        "EXAM_RESULT",
                        "Resultado disponible — " + row.get("exam_name"),
                        "Paciente: " + row.get("patient"),
                        "success",
                        dt != null ? dt.format(FMT) : "",
                        (String) row.get("id")
                ));
            }
        }

        // Facturas pendientes
        if (pendingInvoices > 0) {
            List<Map<String, Object>> invoices = jdbc.queryForList("""
                    SELECT i.id::text AS id,
                           i.serie || '-' || LPAD(i.number::text, 8, '0') AS code,
                           i.total,
                           p.first_name || ' ' || p.last_name AS patient,
                           i.created_at
                    FROM invoices i
                    JOIN patients p ON i.patient_id = p.id
                    WHERE i.sede_id  = ?
                      AND i.status   = 'PENDING'
                      AND i.created_at >= ?
                    ORDER BY i.created_at DESC
                    LIMIT 5
                    """, sedeId, today.minusDays(30).atStartOfDay());

            for (Map<String, Object> row : invoices) {
                LocalDateTime dt = (LocalDateTime) row.get("created_at");
                items.add(new NotificationItem(
                        "INVOICE",
                        "Pago pendiente — " + row.get("code"),
                        "Paciente: " + row.get("patient") + " | S/ " + row.get("total"),
                        "warning",
                        dt != null ? dt.format(FMT) : "",
                        (String) row.get("id")
                ));
            }
        }

        // Stock bajo
        if (lowStockItems > 0) {
            List<Map<String, Object>> stock = jdbc.queryForList("""
                    SELECT ii.id::text AS id,
                           ii.name,
                           ii.stock_quantity,
                           ii.reorder_point
                    FROM inventory_items ii
                    WHERE ii.sede_id        = ?
                      AND ii.stock_quantity <= ii.reorder_point
                      AND ii.is_active      = true
                    ORDER BY ii.stock_quantity ASC
                    LIMIT 5
                    """, sedeId);

            for (Map<String, Object> row : stock) {
                items.add(new NotificationItem(
                        "STOCK",
                        "Stock bajo — " + row.get("name"),
                        "Disponible: " + row.get("stock_quantity") + " | Mínimo: " + row.get("reorder_point"),
                        "danger",
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " 00:00",
                        (String) row.get("id")
                ));
            }
        }

        int totalUnread = appointmentsToday + pendingExamResults + pendingInvoices + lowStockItems;
        return new NotificationSummaryResponse(
                totalUnread,
                appointmentsToday,
                pendingExamResults,
                pendingInvoices,
                lowStockItems,
                items
        );
    }

    private int count(String sql, Object... params) {
        Integer result = jdbc.queryForObject(sql, Integer.class, params);
        return result != null ? result : 0;
    }
}
