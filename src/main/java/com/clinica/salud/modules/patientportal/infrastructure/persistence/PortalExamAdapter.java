package com.clinica.salud.modules.patientportal.infrastructure.persistence;

import com.clinica.salud.modules.patientportal.application.dto.PortalExamResponse;
import com.clinica.salud.modules.patientportal.domain.port.PortalExamPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PortalExamAdapter implements PortalExamPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<PortalExamResponse> findByPatientId(UUID patientId) {
        String sql = """
                SELECT
                    eo.id         AS order_id,
                    sv.name       AS service_name,
                    eo.status     AS order_status,
                    eoi.result_text,
                    eoi.result_at,
                    eo.created_at
                FROM exam_orders eo
                LEFT JOIN exam_order_items eoi ON eoi.order_id = eo.id
                LEFT JOIN services sv          ON eoi.service_id = sv.id
                WHERE eo.patient_id = ?
                ORDER BY eo.created_at DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> PortalExamResponse.builder()
                .orderId((UUID) rs.getObject("order_id"))
                .serviceName(rs.getString("service_name"))
                .orderStatus(rs.getString("order_status"))
                .resultText(rs.getString("result_text"))
                .resultAt(rs.getObject("result_at", LocalDateTime.class))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .build(), patientId);
    }
}
