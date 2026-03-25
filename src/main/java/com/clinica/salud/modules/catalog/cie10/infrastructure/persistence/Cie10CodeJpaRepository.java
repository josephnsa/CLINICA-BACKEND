package com.clinica.salud.modules.catalog.cie10.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface Cie10CodeJpaRepository extends JpaRepository<Cie10CodeEntity, UUID> {

    @Query("""
            SELECT c FROM Cie10CodeEntity c
            WHERE LOWER(c.code) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(c.description) LIKE LOWER(CONCAT('%', :q, '%'))
            """)
    Page<Cie10CodeEntity> search(@Param("q") String q, Pageable pageable);
}

