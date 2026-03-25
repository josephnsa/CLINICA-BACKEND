package com.clinica.salud.modules.catalog.service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicalServiceJpaRepository extends JpaRepository<ServiceEntity, UUID> {

    Optional<ServiceEntity> findByCode(String code);

    List<ServiceEntity> findBySpecialty_IdAndIsActiveTrue(UUID specialtyId);

    List<ServiceEntity> findByIsActiveTrue();
}

