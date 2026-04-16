package com.clinica.salud.modules.patientportal.infrastructure.persistence;

import com.clinica.salud.modules.patientportal.application.dto.PortalAppointmentResponse;
import com.clinica.salud.modules.patientportal.domain.port.PortalAppointmentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PortalAppointmentAdapter implements PortalAppointmentPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<PortalAppointmentResponse> findByPatientId(UUID patientId) {
        String sql = """
                SELECT
                    a.id,
                    u.full_name                  AS doctor_name,
                    sp.name                      AS specialty_name,
                    sv.name                      AS service_name,
                    se.name                      AS sede_name,
                    a.start_time,
                    a.end_time,
                    a.status,
                    a.notes
                FROM appointments a
                LEFT JOIN doctors d   ON a.doctor_id   = d.id
                LEFT JOIN users u     ON d.user_id      = u.id
                LEFT JOIN specialties sp ON d.specialty_id = sp.id
                LEFT JOIN services sv ON a.service_id   = sv.id
                LEFT JOIN sedes se    ON a.sede_id      = se.id
                WHERE a.patient_id = ?
                ORDER BY a.start_time DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> PortalAppointmentResponse.builder()
                .id((UUID) rs.getObject("id"))
                .doctorName(rs.getString("doctor_name"))
                .specialtyName(rs.getString("specialty_name"))
                .serviceName(rs.getString("service_name"))
                .sedeName(rs.getString("sede_name"))
                .startTime(rs.getObject("start_time", LocalDateTime.class))
                .endTime(rs.getObject("end_time", LocalDateTime.class))
                .status(rs.getString("status"))
                .notes(rs.getString("notes"))
                .build(), patientId);
    }
}
