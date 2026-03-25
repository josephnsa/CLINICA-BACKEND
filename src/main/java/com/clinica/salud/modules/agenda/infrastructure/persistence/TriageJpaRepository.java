package com.clinica.salud.modules.agenda.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TriageJpaRepository extends JpaRepository<TriageEntity, UUID> {

    Optional<TriageEntity> findByAppointmentId(UUID appointmentId);

    List<TriageEntity> findByPatientIdOrderByRecordedAtDesc(UUID patientId);
}
