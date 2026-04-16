package com.clinica.salud.modules.integration.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Scheduler de recordatorios automáticos.
 *
 * Corre diariamente a las 08:00 y envía recordatorios de citas del día siguiente.
 * Requiere que app.reminders.enabled=true para activarse.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final JdbcTemplate jdbcTemplate;
    private final EmailService emailService;

    @Value("${app.reminders.enabled:false}")
    private boolean remindersEnabled;

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Recordatorio de citas del día siguiente.
     * Se ejecuta todos los días a las 08:00 AM.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendAppointmentReminders() {
        if (!remindersEnabled) {
            log.debug("Recordatorios desactivados (app.reminders.enabled=false)");
            return;
        }

        log.info("Ejecutando recordatorios de citas...");

        String sql = """
                SELECT
                    p.email                                  AS patient_email,
                    p.first_name || ' ' || p.last_name       AS patient_name,
                    u.full_name                              AS doctor_name,
                    a.start_time,
                    se.name                                  AS sede_name
                FROM appointments a
                JOIN patients p    ON a.patient_id  = p.id
                JOIN doctors d     ON a.doctor_id   = d.id
                JOIN users u       ON d.user_id     = u.id
                JOIN sedes se      ON a.sede_id     = se.id
                WHERE a.status IN ('PENDING', 'CONFIRMED')
                  AND a.start_time::date = CURRENT_DATE + INTERVAL '1 day'
                  AND p.email IS NOT NULL
                """;

        List<Map<String, Object>> appointments = jdbcTemplate.queryForList(sql);
        log.info("Encontradas {} citas para recordatorio mañana", appointments.size());

        for (Map<String, Object> row : appointments) {
            try {
                String email       = (String) row.get("patient_email");
                String patientName = (String) row.get("patient_name");
                String doctorName  = (String) row.get("doctor_name");
                String startTime   = row.get("start_time") != null
                        ? ((java.time.LocalDateTime) row.get("start_time")).format(DISPLAY_FMT)
                        : "";
                String sedeName    = (String) row.get("sede_name");

                emailService.sendAppointmentReminder(email, patientName, doctorName, startTime, sedeName);
            } catch (Exception e) {
                log.error("Error procesando recordatorio para fila {}: {}", row, e.getMessage());
            }
        }
    }
}
