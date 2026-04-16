package com.clinica.salud.modules.hrm.application.usecase;

import com.clinica.salud.modules.hrm.application.dto.AttendanceResponse;
import com.clinica.salud.modules.hrm.application.dto.RegisterCheckOutRequest;
import com.clinica.salud.modules.hrm.domain.model.AttendanceRecord;
import com.clinica.salud.modules.hrm.domain.port.AttendanceRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterCheckOutUseCase {

    private final AttendanceRepository attendanceRepository;

    @Transactional
    public AttendanceResponse execute(RegisterCheckOutRequest request) {
        AttendanceRecord record = attendanceRepository
                .findByEmployeeIdAndDate(request.getEmployeeId(), LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("AttendanceRecord", request.getEmployeeId().toString()));

        record.registerCheckOut(LocalDateTime.now());
        AttendanceRecord saved = attendanceRepository.save(record);
        return toResponse(saved);
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
