package com.clinica.salud.modules.patientportal.infrastructure.persistence;

import com.clinica.salud.modules.patientportal.application.dto.PortalPrescriptionResponse;
import com.clinica.salud.modules.patientportal.domain.port.PortalPrescriptionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PortalPrescriptionAdapter implements PortalPrescriptionPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<PortalPrescriptionResponse> findByPatientId(UUID patientId) {
        String sql = """
                SELECT
                    p.id              AS prescription_id,
                    p.diagnosis_id    AS diagnosis_code,
                    p.status,
                    p.notes,
                    p.created_at,
                    m.generic_name    AS medication_name,
                    pi.dose,
                    pi.frequency,
                    pi.duration,
                    pi.instructions,
                    pi.is_dispensed
                FROM prescriptions p
                LEFT JOIN prescription_items pi ON pi.prescription_id = p.id
                LEFT JOIN medications m         ON pi.medication_id   = m.id
                WHERE p.patient_id = ?
                ORDER BY p.created_at DESC
                """;

        Map<UUID, PortalPrescriptionResponse> map = new LinkedHashMap<>();

        jdbcTemplate.query(sql, rs -> {
            UUID prescriptionId   = (UUID) rs.getObject("prescription_id");
            String diagnosisCode  = rs.getString("diagnosis_code");
            String status         = rs.getString("status");
            String notes          = rs.getString("notes");
            LocalDateTime createdAt = rs.getObject("created_at", LocalDateTime.class);
            String medicationName = rs.getString("medication_name");
            String dose           = rs.getString("dose");
            String frequency      = rs.getString("frequency");
            String duration       = rs.getString("duration");
            String instructions   = rs.getString("instructions");
            boolean dispensed     = rs.getBoolean("is_dispensed");

            PortalPrescriptionResponse response = map.computeIfAbsent(prescriptionId, id ->
                    PortalPrescriptionResponse.builder()
                            .id(id)
                            .diagnosisCode(diagnosisCode)
                            .status(status)
                            .notes(notes)
                            .createdAt(createdAt)
                            .medications(new ArrayList<>())
                            .build()
            );

            if (medicationName != null) {
                response.getMedications().add(
                        PortalPrescriptionResponse.PrescriptionItemDetail.builder()
                                .medicationName(medicationName)
                                .dose(dose)
                                .frequency(frequency)
                                .duration(duration)
                                .instructions(instructions)
                                .dispensed(dispensed)
                                .build()
                );
            }
        }, patientId);

        return new ArrayList<>(map.values());
    }
}
