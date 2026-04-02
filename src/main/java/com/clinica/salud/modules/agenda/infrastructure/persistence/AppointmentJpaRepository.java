package com.clinica.salud.modules.agenda.infrastructure.persistence;

import com.clinica.salud.modules.agenda.domain.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, UUID> {

    @Query("SELECT e FROM AppointmentEntity e WHERE e.doctor.id = :doctorId AND e.startTime >= :dayStart AND e.startTime < :dayEnd")
    List<AppointmentEntity> findByDoctorIdAndDate(
            @Param("doctorId") UUID doctorId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd);

    @Query("SELECT e FROM AppointmentEntity e WHERE e.patient.id = :patientId")
    Page<AppointmentEntity> findByPatientIdOrderByStartTimeDesc(@Param("patientId") UUID patientId, Pageable pageable);

    @Query("SELECT e FROM AppointmentEntity e ORDER BY e.startTime DESC")
    Page<AppointmentEntity> findAllByOrderByStartTimeDesc(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AppointmentEntity a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.status NOT IN (:cancelledStatus, :noShowStatus) " +
            "AND a.startTime < :end AND a.endTime > :start " +
            "AND (:excludeId IS NULL OR a.id <> :excludeId)")
    boolean existsByDoctorAndTimeOverlap(
            @Param("doctorId") UUID doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("excludeId") UUID excludeId,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus,
            @Param("noShowStatus") AppointmentStatus noShowStatus);

    @Query("SELECT e FROM AppointmentEntity e WHERE e.sede.id = :sedeId AND e.startTime < :end AND e.endTime > :start")
    List<AppointmentEntity> findBySedeIdAndTimeOverlap(
            @Param("sedeId") UUID sedeId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
