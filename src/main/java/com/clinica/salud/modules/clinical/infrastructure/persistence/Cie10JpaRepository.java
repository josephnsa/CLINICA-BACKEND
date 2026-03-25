package com.clinica.salud.modules.clinical.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface Cie10JpaRepository extends JpaRepository<Cie10Entity, UUID> {

    boolean existsByCode(String code);
}
