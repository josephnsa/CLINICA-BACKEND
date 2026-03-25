package com.clinica.salud.modules.patients.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsentJpaRepository extends JpaRepository<ConsentEntity, UUID> {

    List<ConsentEntity> findByPatientIdOrderBySignedAtDesc(UUID patientId);
}
