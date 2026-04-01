package com.clinica.salud.modules.catalog.cie10.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface Cie10CodeJpaRepository extends JpaRepository<Cie10CodeEntity, UUID> {

    /**
     * Filtro por código y/o descripción. Cadenas vacías se traducen a LIKE '%%' (sin filtrar ese campo).
     */
    @Query("""
            SELECT c FROM Cie10CodeEntity c
            WHERE LOWER(c.code) LIKE LOWER(CONCAT('%', :code, '%'))
              AND LOWER(c.description) LIKE LOWER(CONCAT('%', :desc, '%'))
            """)
    Page<Cie10CodeEntity> searchFiltered(
            @Param("code") String code,
            @Param("desc") String desc,
            Pageable pageable);
}

