package com.clinica.salud.modules.dashboard.infrastructure.persistence;

import com.clinica.salud.modules.dashboard.application.dto.*;
import com.clinica.salud.modules.dashboard.domain.port.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class DashboardRepositoryImpl implements DashboardRepository {

    private final JdbcTemplate jdbc;

    // ── 1. SUMMARY ────────────────────────────────────────────────────────────

    @Override
    public DashboardSummaryResponse getSummary(UUID sedeId, int year, int month) {
        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        LocalDate firstOfNextMonth = firstOfMonth.plusMonths(1);
        LocalDate firstOfPrevMonth = firstOfMonth.minusMonths(1);

        // Ingresos mes actual y mes anterior
        Map<String, Object> rev = jdbc.queryForMap("""
                SELECT
                    COALESCE(SUM(total) FILTER (
                        WHERE created_at >= ? AND created_at < ?), 0) AS current_month,
                    COALESCE(SUM(total) FILTER (
                        WHERE created_at >= ? AND created_at < ?), 0) AS prev_month
                FROM invoices
                WHERE sede_id = ?
                  AND status  = 'PAID'
                  AND created_at >= ?
                  AND created_at < ?
                """,
                firstOfMonth, firstOfNextMonth,
                firstOfPrevMonth, firstOfMonth,
                sedeId,
                firstOfPrevMonth, firstOfNextMonth);

        BigDecimal currentMonth = toBD(rev.get("current_month"));
        BigDecimal prevMonth    = toBD(rev.get("prev_month"));
        double momChange = calcGrowth(currentMonth, prevMonth);

        // Ingresos año actual y año anterior
        Map<String, Object> yearly = jdbc.queryForMap("""
                SELECT
                    COALESCE(SUM(total) FILTER (
                        WHERE EXTRACT(YEAR FROM created_at) = ?), 0) AS current_year,
                    COALESCE(SUM(total) FILTER (
                        WHERE EXTRACT(YEAR FROM created_at) = ? - 1), 0) AS prev_year
                FROM invoices
                WHERE sede_id = ?
                  AND status  = 'PAID'
                  AND EXTRACT(YEAR FROM created_at) BETWEEN (? - 1) AND ?
                """, year, year, sedeId, year, year);

        BigDecimal currentYear = toBD(yearly.get("current_year"));
        BigDecimal prevYear    = toBD(yearly.get("prev_year"));
        double yoyChange = calcGrowth(currentYear, prevYear);

        // Citas del mes
        Map<String, Object> appts = jdbc.queryForMap("""
                SELECT COUNT(*) AS month_appointments
                FROM appointments
                WHERE sede_id   = ?
                  AND start_time >= ?
                  AND start_time <  ?
                  AND status NOT IN ('CANCELLED', 'NO_SHOW')
                """, sedeId, firstOfMonth.atStartOfDay(), firstOfNextMonth.atStartOfDay());

        long monthAppointments = toLong(appts.get("month_appointments"));

        // Pacientes nuevos (primera cita registrada a partir del primer día del mes)
        Map<String, Object> newPats = jdbc.queryForMap("""
                SELECT COUNT(*) AS new_patients
                FROM patients
                WHERE created_at >= ?
                  AND created_at <  ?
                  AND id IN (
                      SELECT DISTINCT patient_id
                      FROM appointments
                      WHERE sede_id = ?
                  )
                """, firstOfMonth.atStartOfDay(), firstOfNextMonth.atStartOfDay(), sedeId);

        long newPatients = toLong(newPats.get("new_patients"));

        return new DashboardSummaryResponse(
                currentMonth, momChange,
                currentYear,  yoyChange,
                monthAppointments, newPatients);
    }

    // ── 2. MONTHLY REVENUE ────────────────────────────────────────────────────

    @Override
    public List<RevenueMonthResponse> getMonthlyRevenue(UUID sedeId, int year) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT
                    EXTRACT(MONTH FROM created_at)::int AS month,
                    COALESCE(SUM(total), 0)             AS total_invoiced,
                    COUNT(*)                            AS invoice_count
                FROM invoices
                WHERE sede_id = ?
                  AND EXTRACT(YEAR FROM created_at) = ?
                  AND status = 'PAID'
                GROUP BY EXTRACT(MONTH FROM created_at)
                ORDER BY month
                """, sedeId, year);

        // Índice por número de mes
        Map<Integer, Map<String, Object>> byMonth = new HashMap<>();
        for (Map<String, Object> row : rows) {
            byMonth.put((Integer) row.get("month"), row);
        }

        List<RevenueMonthResponse> result = new ArrayList<>(12);
        for (int m = 1; m <= 12; m++) {
            Map<String, Object> row = byMonth.get(m);
            BigDecimal total = row != null ? toBD(row.get("total_invoiced")) : BigDecimal.ZERO;
            long       count = row != null ? toLong(row.get("invoice_count"))  : 0L;
            String label = Month.of(m).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            result.add(new RevenueMonthResponse(m, label, total, count));
        }
        return result;
    }

    // ── 3. YEARLY BREAKUP ─────────────────────────────────────────────────────

    @Override
    public YearlyBreakupResponse getYearlyBreakup(UUID sedeId) {
        int currentYear = LocalDate.now().getYear();

        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT
                    EXTRACT(YEAR FROM created_at)::int AS year,
                    COALESCE(SUM(total), 0)            AS total_revenue
                FROM invoices
                WHERE sede_id = ?
                  AND status  = 'PAID'
                  AND EXTRACT(YEAR FROM created_at) BETWEEN ? AND ?
                GROUP BY EXTRACT(YEAR FROM created_at)
                ORDER BY year DESC
                """, sedeId, currentYear - 2, currentYear);

        // Aseguramos los 3 años aunque no haya datos
        Map<Integer, BigDecimal> byYear = new LinkedHashMap<>();
        for (int y = currentYear; y >= currentYear - 2; y--) byYear.put(y, BigDecimal.ZERO);
        for (Map<String, Object> row : rows) {
            byYear.put((Integer) row.get("year"), toBD(row.get("total_revenue")));
        }

        List<YearlyBreakupResponse.YearSummary> summaries = new ArrayList<>();
        BigDecimal prevRevenue = null;
        // Iteramos de más antiguo a más reciente para calcular crecimiento
        List<Integer> years = new ArrayList<>(byYear.keySet());
        Collections.sort(years);
        for (int y : years) {
            BigDecimal rev = byYear.get(y);
            Double growth = prevRevenue != null ? calcGrowth(rev, prevRevenue) : null;
            summaries.add(new YearlyBreakupResponse.YearSummary(y, rev, growth));
            prevRevenue = rev;
        }
        // Revertir a orden descendente (más reciente primero)
        Collections.reverse(summaries);
        return new YearlyBreakupResponse(summaries);
    }

    // ── 4. RECENT TRANSACTIONS ────────────────────────────────────────────────

    @Override
    public List<RecentTransactionResponse> getRecentTransactions(UUID sedeId, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 50);

        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT
                    i.id::text                                       AS invoice_id,
                    p.paid_at,
                    pt.first_name || ' ' || pt.last_name             AS patient_full_name,
                    COALESCE(
                        (SELECT ii.description
                         FROM invoice_items ii
                         WHERE ii.invoice_id = i.id
                         ORDER BY ii.id
                         LIMIT 1),
                        i.serie || '-' || LPAD(i.number::text, 8, '0')
                    )                                                AS description,
                    p.amount,
                    i.status,
                    p.method                                         AS payment_method
                FROM payments p
                JOIN invoices i   ON p.invoice_id = i.id
                JOIN patients pt  ON i.patient_id  = pt.id
                WHERE i.sede_id = ?
                ORDER BY p.paid_at DESC
                LIMIT ?
                """, sedeId, safeLimit);

        List<RecentTransactionResponse> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            result.add(new RecentTransactionResponse(
                    (String)        row.get("invoice_id"),
                    toDateTime(     row.get("paid_at")),
                    (String)        row.get("patient_full_name"),
                    (String)        row.get("description"),
                    toBD(           row.get("amount")),
                    (String)        row.get("status"),
                    (String)        row.get("payment_method")
            ));
        }
        return result;
    }

    // ── 5. SERVICE PERFORMANCE (raw — sin badge) ──────────────────────────────

    @Override
    public List<ServicePerformanceResponse> getServicePerformanceRaw(UUID sedeId, int month, int year) {
        LocalDate firstOfMonth    = LocalDate.of(year, month, 1);
        LocalDate firstOfNextMonth = firstOfMonth.plusMonths(1);

        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT
                    d.id::text                               AS doctor_id,
                    u.full_name                              AS doctor_name,
                    sv.name                                  AS service_name,
                    COUNT(a.id)                              AS appointment_count,
                    COALESCE(SUM(i.total), 0)                AS revenue_generated
                FROM appointments a
                JOIN doctors  d  ON a.doctor_id  = d.id
                JOIN users    u  ON d.user_id     = u.id
                JOIN services sv ON a.service_id  = sv.id
                LEFT JOIN invoices i
                    ON  i.appointment_id = a.id
                    AND i.status = 'PAID'
                WHERE a.sede_id    = ?
                  AND a.start_time >= ?
                  AND a.start_time <  ?
                  AND a.status = 'ATTENDED'
                GROUP BY d.id, u.full_name, sv.name
                ORDER BY revenue_generated DESC
                """,
                sedeId,
                firstOfMonth.atStartOfDay(),
                firstOfNextMonth.atStartOfDay());

        List<ServicePerformanceResponse> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            result.add(new ServicePerformanceResponse(
                    UUID.fromString((String) row.get("doctor_id")),
                    (String) row.get("doctor_name"),
                    (String) row.get("service_name"),
                    toLong(row.get("appointment_count")),
                    toBD(row.get("revenue_generated")),
                    "LOW"   // placeholder — el use case asigna el badge real
            ));
        }
        return result;
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private BigDecimal toBD(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal bd) return bd;
        try { return new BigDecimal(val.toString()); }
        catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }

    private long toLong(Object val) {
        if (val == null) return 0L;
        if (val instanceof Number n) return n.longValue();
        try { return Long.parseLong(val.toString()); }
        catch (NumberFormatException e) { return 0L; }
    }

    private LocalDateTime toDateTime(Object val) {
        if (val instanceof LocalDateTime ldt) return ldt;
        return null;
    }

    private double calcGrowth(BigDecimal current, BigDecimal prev) {
        if (prev == null || prev.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return current.subtract(prev)
                .divide(prev, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
