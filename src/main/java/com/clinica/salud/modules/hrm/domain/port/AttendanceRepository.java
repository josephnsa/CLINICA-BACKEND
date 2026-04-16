package com.clinica.salud.modules.hrm.domain.port;

import com.clinica.salud.modules.hrm.domain.model.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository {

    AttendanceRecord save(AttendanceRecord record);

    Optional<AttendanceRecord> findByEmployeeIdAndDate(UUID employeeId, LocalDate date);

    List<AttendanceRecord> findByEmployeeId(UUID employeeId);

    List<AttendanceRecord> findByEmployeeIdAndDateBetween(UUID employeeId, LocalDate from, LocalDate to);

    List<AttendanceRecord> findBySedeIdAndDate(UUID sedeId, LocalDate date);

    boolean existsByEmployeeIdAndDate(UUID employeeId, LocalDate date);
}
