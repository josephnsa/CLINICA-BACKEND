package com.clinica.salud.modules.hrm.application.usecase;

import com.clinica.salud.modules.hrm.application.dto.AttendanceResponse;
import com.clinica.salud.modules.hrm.domain.model.AttendanceRecord;
import com.clinica.salud.modules.hrm.domain.port.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListAttendanceUseCase {

    private final AttendanceRepository attendanceRepository;

    @Transactional(readOnly = true)
    public List<AttendanceResponse> execute(UUID employeeId, LocalDate from, LocalDate to) {
        List<AttendanceRecord> records;

        if (from != null && to != null) {
            records = attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, from, to);
        } else {
            records = attendanceRepository.findByEmployeeId(employeeId);
        }

        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private AttendanceResponse toResponse(AttendanceRecord r) {
        return AttendanceResponse.builder()
                .id(r.getId())
                .employeeId(r.getEmployeeId())
                .sedeId(r.getSedeId())
                .date(r.getDate())
                .checkIn(r.getCheckIn())
                .checkOut(r.getCheckOut())
                .minutesWorked(r.getMinutesWorked())
                .status(r.getStatus())
                .notes(r.getNotes())
                .build();
    }
}
