package com.clinica.salud.modules.clinical.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClinicalNoteJpaRepository extends JpaRepository<ClinicalNoteEntity, UUID> {

    Page<ClinicalNoteEntity> findByPatientIdOrderByCreatedAtDesc(UUID patientId, Pageable pageable);
}
