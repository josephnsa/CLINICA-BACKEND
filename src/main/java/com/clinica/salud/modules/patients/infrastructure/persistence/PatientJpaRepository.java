package com.clinica.salud.modules.patients.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PatientJpaRepository extends JpaRepository<PatientEntity, UUID> {

    Optional<PatientEntity> findByDocNumber(String docNumber);

    boolean existsByDocNumber(String docNumber);

    @Query("SELECT p FROM PatientEntity p WHERE " +
            "(:query IS NULL OR :query = '' OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.docNumber) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:docNumber IS NULL OR :docNumber = '' OR p.docNumber = :docNumber) " +
            "AND (:activeOnly = false OR p.isActive = true)")
    Page<PatientEntity> search(
            @Param("query") String query,
            @Param("docNumber") String docNumber,
            @Param("activeOnly") boolean activeOnly,
            Pageable pageable);
}
