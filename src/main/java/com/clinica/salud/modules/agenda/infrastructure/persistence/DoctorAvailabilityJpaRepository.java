package com.clinica.salud.modules.agenda.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DoctorAvailabilityJpaRepository extends JpaRepository<DoctorAvailabilityEntity, UUID> {

    List<DoctorAvailabilityEntity> findByDoctorId(UUID doctorId);

    List<DoctorAvailabilityEntity> findByDoctorIdAndActiveTrue(UUID doctorId);
}
