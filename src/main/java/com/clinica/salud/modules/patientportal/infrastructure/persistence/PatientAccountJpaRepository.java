package com.clinica.salud.modules.patientportal.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PatientAccountJpaRepository extends JpaRepository<PatientAccountEntity, UUID> {

    Optional<PatientAccountEntity> findByEmail(String email);

    Optional<PatientAccountEntity> findByPatientId(UUID patientId);

    boolean existsByEmail(String email);
}
