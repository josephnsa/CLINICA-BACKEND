package com.clinica.salud.modules.agenda.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DoctorJpaRepository extends JpaRepository<DoctorEntity, UUID> {

    java.util.Optional<DoctorEntity> findByUserId(UUID userId);
}
