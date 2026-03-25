package com.clinica.salud.modules.agenda.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AvailabilityBlockJpaRepository extends JpaRepository<AvailabilityBlockEntity, UUID> {

    List<AvailabilityBlockEntity> findByDoctorId(UUID doctorId);

    @Query("SELECT b FROM AvailabilityBlockEntity b WHERE b.doctorId = :doctorId " +
           "AND b.startDt < :to AND b.endDt > :from")
    List<AvailabilityBlockEntity> findActiveByDoctorAndRange(
            @Param("doctorId") UUID doctorId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
