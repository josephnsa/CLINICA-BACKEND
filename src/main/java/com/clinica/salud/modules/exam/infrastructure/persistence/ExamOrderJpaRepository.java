package com.clinica.salud.modules.exam.infrastructure.persistence;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExamOrderJpaRepository extends JpaRepository<ExamOrderEntity, UUID> {

    @EntityGraph(attributePaths = "items")
    Optional<ExamOrderEntity> findWithItemsById(UUID id);

    @EntityGraph(attributePaths = "items")
    List<ExamOrderEntity> findByPatientId(UUID patientId);
}

