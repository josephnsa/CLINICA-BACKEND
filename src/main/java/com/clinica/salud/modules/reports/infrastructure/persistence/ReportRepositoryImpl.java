package com.clinica.salud.modules.reports.infrastructure.persistence;

import com.clinica.salud.modules.reports.application.dto.FinancialReportResponse;
import com.clinica.salud.modules.reports.application.dto.InventoryReportResponse;
import com.clinica.salud.modules.reports.application.dto.OperationalReportResponse;
import com.clinica.salud.modules.reports.domain.port.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {

    private final JdbcTemplate jdbc;

    // ─── REPORTE OPERATIVO ────────────────────────────────────────────────────

    @Override
    public OperationalReportResponse getOperationalReport(UUID sedeId, LocalDate from, LocalDate to) {
        String sql = """
                SELECT
                    COUNT(*)                                                    AS total,
                    SUM(CASE WHEN status = 'ATTENDED'  THEN 1 ELSE 0 END)      AS attended,
                    SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END)      AS cancelled,
                    SUM(CASE WHEN status = 'NO_SHOW'   THEN 1 ELSE 0 END)      AS no_show,
                    ROUND(AVG(EXTRACT(EPOCH FROM (end_time - start_time)) / 60)::numeric, 1)
                                                                                AS avg_duration_min
                FROM appointments
                WHERE sede_id = ?
                  AND start_time::date BETWEEN ? AND ?
                """;

        Map<String, Object> row = jdbc.queryForMap(sql,
                sedeId, from, to);

        long total     = toLong(row.get("total"));
        long attended  = toLong(row.get("attended"));
        long cancelled = toLong(row.get("cancelled"));
        long noShow    = toLong(row.get("no_show"));
        double avgMin  = toDouble(row.get("avg_duration_min"));

        double attendanceRate = total > 0 ? (attended  * 100.0 / total) : 0;
        double noShowRate     = total > 0 ? (noShow    * 100.0 / total) : 0;

        return OperationalReportResponse.builder()
                .sedeId(sedeId)
                .dateFrom(from)
                .dateTo(to)
                .totalAppointments(total)
                .attendedAppointments(attended)
                .cancelledAppointments(cancelled)
                .noShowAppointments(noShow)
                .attendanceRate(Math.round(attendanceRate * 10.0) / 10.0)
                .noShowRate(Math.round(noShowRate * 10.0) / 10.0)
                .avgWaitTimeMinutes(0)          // requiere campo checkin_time — pendiente
                .avgConsultationMinutes(avgMin)
                .build();
    }

    // ─── REPORTE FINANCIERO ───────────────────────────────────────────────────

    @Override
    public FinancialReportResponse getFinancialReport(UUID sedeId, LocalDate from, LocalDate to) {
        String invoiceSql = """
                SELECT
                    COUNT(*)                                                         AS total_invoices,
                    SUM(total)                                                       AS total_billed,
                    SUM(CASE WHEN status = 'PAID'      THEN total ELSE 0 END)       AS total_paid,
                    SUM(CASE WHEN status = 'PENDING'   THEN total ELSE 0 END)       AS total_pending,
                    SUM(CASE WHEN status = 'REFUNDED'  THEN total ELSE 0 END)       AS total_refunded,
                    SUM(CASE WHEN status = 'PAID'      THEN 1     ELSE 0 END)       AS paid_count,
                    SUM(CASE WHEN status = 'PENDING'   THEN 1     ELSE 0 END)       AS pending_count,
                    ROUND(AVG(total)::numeric, 2)                                   AS avg_invoice
                FROM invoices
                WHERE sede_id = ?
                  AND created_at::date BETWEEN ? AND ?
                """;

        Map<String, Object> inv = jdbc.queryForMap(invoiceSql, sedeId, from, to);

        String paymentSql = """
                SELECT
                    SUM(CASE WHEN p.method = 'CASH'     THEN p.amount ELSE 0 END)  AS cash,
                    SUM(CASE WHEN p.method = 'CARD'     THEN p.amount ELSE 0 END)  AS card,
                    SUM(CASE WHEN p.method = 'TRANSFER' THEN p.amount ELSE 0 END)  AS transfer,
                    SUM(CASE WHEN p.method = 'INSURANCE' THEN p.amount ELSE 0 END) AS insurance
                FROM payments p
                JOIN invoices i ON p.invoice_id = i.id
                WHERE i.sede_id = ?
                  AND p.paid_at::date BETWEEN ? AND ?
                """;

        Map<String, Object> pay = jdbc.queryForMap(paymentSql, sedeId, from, to);

        return FinancialReportResponse.builder()
                .sedeId(sedeId)
                .dateFrom(from)
                .dateTo(to)
                .totalInvoices(toLong(inv.get("total_invoices")))
                .totalBilled(toBigDecimal(inv.get("total_billed")))
                .totalCollected(toBigDecimal(inv.get("total_paid")))
                .totalPending(toBigDecimal(inv.get("total_pending")))
                .totalRefunded(toBigDecimal(inv.get("total_refunded")))
                .paidInvoices(toLong(inv.get("paid_count")))
                .pendingInvoices(toLong(inv.get("pending_count")))
                .avgInvoiceAmount(toBigDecimal(inv.get("avg_invoice")))
                .cashAmount(toBigDecimal(pay.get("cash")))
                .cardAmount(toBigDecimal(pay.get("card")))
                .transferAmount(toBigDecimal(pay.get("transfer")))
                .insuranceAmount(toBigDecimal(pay.get("insurance")))
                .build();
    }

    // ─── REPORTE DE INVENTARIO ────────────────────────────────────────────────

    @Override
    public InventoryReportResponse getInventoryReport(UUID sedeId, LocalDate reportDate) {
        String summarySql = """
                SELECT
                    COUNT(*)                                                    AS total_items,
                    SUM(CASE WHEN stock <= min_stock AND stock > 0 THEN 1 ELSE 0 END) AS low_stock,
                    SUM(CASE WHEN stock = 0           THEN 1 ELSE 0 END)        AS out_of_stock
                FROM inventory_items
                WHERE sede_id = ?
                """;

        Map<String, Object> summary = jdbc.queryForMap(summarySql, sedeId);

        // Ítems con vencimiento próximo (30 días) usando inventory_movements
        String expiringSql = """
                SELECT COUNT(DISTINCT item_id) AS expiring
                FROM inventory_movements
                WHERE expiry_date BETWEEN ? AND ?
                  AND type = 'IN'
                """;

        Map<String, Object> expiring = jdbc.queryForMap(expiringSql,
                reportDate, reportDate.plusDays(30));

        String expiredSql = """
                SELECT COUNT(DISTINCT item_id) AS expired
                FROM inventory_movements
                WHERE expiry_date < ?
                  AND type = 'IN'
                """;

        Map<String, Object> expired = jdbc.queryForMap(expiredSql, reportDate);

        // Ítems críticos (bajo stock u con vencimiento próximo)
        String criticalSql = """
                SELECT
                    ii.id                       AS item_id,
                    m.name                      AS medication_name,
                    ii.stock                    AS current_stock,
                    ii.min_stock                AS min_stock,
                    MIN(im.expiry_date)         AS nearest_expiry,
                    CASE
                        WHEN ii.stock = 0                          THEN 'OUT_OF_STOCK'
                        WHEN ii.stock <= ii.min_stock              THEN 'LOW_STOCK'
                        WHEN MIN(im.expiry_date) <= ?              THEN 'EXPIRING_SOON'
                        ELSE 'OK'
                    END                         AS alert_type
                FROM inventory_items ii
                JOIN medications m ON ii.medication_id = m.id
                LEFT JOIN inventory_movements im ON im.item_id = ii.id AND im.type = 'IN'
                WHERE ii.sede_id = ?
                GROUP BY ii.id, m.name, ii.stock, ii.min_stock
                HAVING ii.stock <= ii.min_stock
                    OR MIN(im.expiry_date) <= ?
                ORDER BY alert_type, ii.stock ASC
                LIMIT 20
                """;

        LocalDate expiryThreshold = reportDate.plusDays(30);
        List<Map<String, Object>> criticalRows = jdbc.queryForList(criticalSql,
                expiryThreshold, sedeId, expiryThreshold);

        List<InventoryReportResponse.InventoryItemSummary> criticalItems = new ArrayList<>();
        for (Map<String, Object> r : criticalRows) {
            Object expiryObj = r.get("nearest_expiry");
            LocalDate nearestExpiry = expiryObj != null
                    ? ((java.sql.Date) expiryObj).toLocalDate()
                    : null;

            criticalItems.add(InventoryReportResponse.InventoryItemSummary.builder()
                    .medicationId(UUID.fromString(r.get("item_id").toString()))
                    .medicationName((String) r.get("medication_name"))
                    .currentStock(((Number) r.get("current_stock")).intValue())
                    .minStock(((Number) r.get("min_stock")).intValue())
                    .nearestExpiry(nearestExpiry)
                    .alertType((String) r.get("alert_type"))
                    .build());
        }

        return InventoryReportResponse.builder()
                .sedeId(sedeId)
                .reportDate(reportDate)
                .totalItems(toLong(summary.get("total_items")))
                .lowStockItems(toLong(summary.get("low_stock")))
                .outOfStockItems(toLong(summary.get("out_of_stock")))
                .expiringIn30Days(toLong(expiring.get("expiring")))
                .expiredItems(toLong(expired.get("expired")))
                .criticalItems(criticalItems)
                .build();
    }

    // ─── Helpers de conversión segura ─────────────────────────────────────────

    private long toLong(Object val) {
        if (val == null) return 0L;
        return ((Number) val).longValue();
    }

    private double toDouble(Object val) {
        if (val == null) return 0.0;
        return ((Number) val).doubleValue();
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal bd) return bd;
        return new BigDecimal(val.toString());
    }
}
