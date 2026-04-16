package com.clinica.salud.modules.patientportal.infrastructure.persistence;

import com.clinica.salud.modules.patientportal.domain.port.PatientLookupPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PatientLookupAdapter implements PatientLookupPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<PatientInfo> findByEmail(String email) {
        String sql = """
                SELECT id, first_name, last_name, email
                FROM patients
                WHERE LOWER(email) = LOWER(?) AND is_active = true
                LIMIT 1
                """;
        var results = jdbcTemplate.query(sql, (rs, rowNum) -> new PatientInfo(
                (java.util.UUID) rs.getObject("id"),
                rs.getString("first_name") + " " + rs.getString("last_name"),
                rs.getString("email")
        ), email);

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
