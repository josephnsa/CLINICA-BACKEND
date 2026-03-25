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

    @Query("""
            SELECT m FROM MedicationEntity m
            WHERE m.isActive = true
              AND (
                    LOWER(m.genericName) LIKE LOWER(CONCAT('%', :q, '%'))
                 OR LOWER(m.commercialName) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            """)
    Page<MedicationEntity> searchActive(@Param("q") String query, Pageable pageable);
}

