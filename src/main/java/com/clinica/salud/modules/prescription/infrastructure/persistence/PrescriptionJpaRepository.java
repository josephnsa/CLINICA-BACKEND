package com.clinica.salud.modules.prescription.infrastructure.persistence;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrescriptionJpaRepository extends JpaRepository<PrescriptionEntity, UUID> {

    @EntityGraph(attributePaths = "items")
    Optional<PrescriptionEntity> findWithItemsById(UUID id);

    @EntityGraph(attributePaths = "items")
    List<PrescriptionEntity> findByPatientId(UUID patientId);
}

