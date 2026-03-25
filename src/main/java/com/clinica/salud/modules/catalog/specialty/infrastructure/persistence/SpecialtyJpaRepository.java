package com.clinica.salud.modules.catalog.specialty.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecialtyJpaRepository extends JpaRepository<SpecialtyEntity, UUID> {

    Optional<SpecialtyEntity> findByCodeAndIsActiveTrue(String code);

    List<SpecialtyEntity> findByIsActiveTrue();
}

