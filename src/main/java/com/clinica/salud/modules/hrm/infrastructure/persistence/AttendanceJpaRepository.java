package com.clinica.salud.modules.hrm.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceJpaRepository extends JpaRepository<AttendanceEntity, UUID> {

    Optional<AttendanceEntity> findByEmployeeIdAndDate(UUID employeeId, LocalDate date);

    List<AttendanceEntity> findByEmployeeId(UUID employeeId);

    List<AttendanceEntity> findByEmployeeIdAndDateBetween(UUID employeeId, LocalDate from, LocalDate to);

    List<AttendanceEntity> findBySedeIdAndDate(UUID sedeId, LocalDate date);

    boolean existsByEmployeeIdAndDate(UUID employeeId, LocalDate date);
}
