package com.clinica.salud.modules.catalog.medication.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MedicationJpaRepository extends JpaRepository<MedicationEntity, UUID> {

    Optional<MedicationEntity> findByCode(String code);

    Page<MedicationEntity> findByIsActiveTrue(Pageable pageable);

    @Query("""
            SELECT m FROM MedicationEntity m
            WHERE (
                   LOWER(m.code) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(COALESCE(m.genericName,'')) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(COALESCE(m.commercialName,'')) LIKE LOWER(CONCAT('%', :q, '%'))
              )
              AND (:activeOnly = false OR m.isActive = true)
            """)
    Page<MedicationEntity> searchByTerm(
            @Param("q") String q,
            @Param("activeOnly") boolean activeOnly,
            Pageable pageable);
}

