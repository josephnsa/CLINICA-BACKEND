package com.clinica.salud.modules.patients.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClinicalProfileJpaRepository extends JpaRepository<ClinicalProfileEntity, UUID> {

    Optional<ClinicalProfileEntity> findByPatientId(UUID patientId);
}
