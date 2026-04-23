package com.clinica.salud.modules.catalog.public_.infrastructure.web;

import com.clinica.salud.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * API pública — no requiere autenticación.
 * Expone catálogo de médicos, especialidades y disponibilidad para
 * integraciones externas (web corporativa, apps de terceros).
 * Acceso permitido vía SecurityConfig: /api/public/**
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicCatalogController {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Lista médicos activos con su especialidad y sede.
     * Permite filtrar por specialtyId o sedeId.
     */
    @GetMapping("/doctors")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listDoctors(
            @RequestParam(required = false) UUID specialtyId,
            @RequestParam(required = false) UUID sedeId) {

        StringBuilder sql = new StringBuilder("""
                SELECT d.id,
                       u.full_name        AS name,
                       s.name             AS specialty,
                       (
                           SELECT se.name
                           FROM user_sedes us
                           JOIN sedes se ON se.id = us.sede_id
                           WHERE us.user_id = u.id
                           ORDER BY se.name
                           LIMIT 1
                       )                  AS sede,
                       CAST(0 AS NUMERIC(10,2)) AS consultationFee,
                       d.is_active        AS available
                FROM doctors d
                JOIN users u       ON d.user_id      = u.id
                JOIN specialties s ON d.specialty_id = s.id
                WHERE d.is_active = true
                  AND u.is_active  = true
                """);

        List<Object> params = new java.util.ArrayList<>();
        if (specialtyId != null) { sql.append("  AND d.specialty_id = ?\n"); params.add(specialtyId); }
        if (sedeId      != null) {
            sql.append("""
                      AND EXISTS (
                          SELECT 1
                          FROM user_sedes usf
                          WHERE usf.user_id = u.id
                            AND usf.sede_id = ?
                      )
                    """);
            params.add(sedeId);
        }
        sql.append("ORDER BY u.full_name");

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /**
     * Lista todas las especialidades médicas activas.
     */
    @GetMapping("/specialties")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listSpecialties() {
        List<Map<String, Object>> result = jdbcTemplate.queryForList("""
                SELECT id, name, description
                FROM specialties
                ORDER BY name
                """);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /**
     * Devuelve los slots de disponibilidad de un médico para una fecha dada.
     * Retorna los horarios programados que aún no tienen cita confirmada.
     *
     * @param doctorId UUID del médico
     * @param date     Fecha en formato yyyy-MM-dd
     */
    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAvailability(
            @RequestParam UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Slots disponibles = horarios del médico ese día que no tienen cita PENDING/CONFIRMED
        List<Map<String, Object>> slots = jdbcTemplate.queryForList("""
                SELECT sch.id            AS scheduleId,
                       sch.start_time    AS startTime,
                       sch.end_time      AS endTime,
                       se.name           AS sede
                FROM doctor_availability sch
                JOIN sedes se ON sch.sede_id = se.id
                WHERE sch.doctor_id   = ?
                  AND sch.day_of_week = ?
                  AND sch.is_active   = true
                  AND NOT EXISTS (
                      SELECT 1 FROM appointments a
                      WHERE a.doctor_id     = sch.doctor_id
                        AND a.start_time::date = ?
                        AND a.start_time::time >= sch.start_time
                        AND a.start_time::time <  sch.end_time
                        AND a.status IN ('PENDING','CONFIRMED')
                  )
                ORDER BY sch.start_time
                """,
                doctorId,
                date.getDayOfWeek().getValue(),   // 1=Monday … 7=Sunday
                date);

        return ResponseEntity.ok(ApiResponse.ok(slots));
    }
}
