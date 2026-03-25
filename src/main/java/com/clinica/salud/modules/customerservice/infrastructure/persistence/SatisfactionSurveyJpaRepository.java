package com.clinica.salud.modules.customerservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SatisfactionSurveyJpaRepository extends JpaRepository<SatisfactionSurveyEntity, UUID> {

    List<SatisfactionSurveyEntity> findByPatientId(UUID patientId);

    List<SatisfactionSurveyEntity> findByAppointmentId(UUID appointmentId);

    @Query(value = "SELECT AVG(s.score) FROM satisfaction_surveys s " +
                   "JOIN appointments a ON s.appointment_id = a.id " +
                   "WHERE a.sede_id = :sedeId",
           nativeQuery = true)
    Double findAverageScoreBySedeId(@Param("sedeId") UUID sedeId);
}
