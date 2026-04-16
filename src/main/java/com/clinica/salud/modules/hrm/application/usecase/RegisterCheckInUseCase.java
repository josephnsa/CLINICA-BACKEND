package com.clinica.salud.modules.hrm.application.usecase;

import com.clinica.salud.modules.hrm.application.dto.AttendanceResponse;
import com.clinica.salud.modules.hrm.application.dto.RegisterCheckInRequest;
import com.clinica.salud.modules.hrm.domain.model.AttendanceRecord;
import com.clinica.salud.modules.hrm.domain.model.AttendanceStatus;
import com.clinica.salud.modules.hrm.domain.model.Employee;
import com.clinica.salud.modules.hrm.domain.model.EmployeeSchedule;
import com.clinica.salud.modules.hrm.domain.port.AttendanceRepository;
import com.clinica.salud.modules.hrm.domain.port.EmployeeRepository;
import com.clinica.salud.modules.hrm.domain.port.ScheduleRepository;
import com.clinica.salud.shared.exception.BusinessRuleException;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegisterCheckInUseCase {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public AttendanceResponse execute(RegisterCheckInRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId().toString()));

        if (!employee.isActive()) {
            throw new BusinessRuleException("El empleado no está activo");
        }

        LocalDate today = LocalDate.now();
        if (attendanceRepository.existsByEmployeeIdAndDate(request.getEmployeeId(), today)) {
            throw new BusinessRuleException("Ya existe registro de entrada para el empleado en el día de hoy");
        }

        LocalDateTime now = LocalDateTime.now();
        AttendanceRecord record = AttendanceRecord.builder()
                .employeeId(request.getEmployeeId())
                .sedeId(request.getSedeId())
                .date(today)
                .checkIn(now)
                .status(AttendanceStatus.PRESENT)
                .notes(request.getNotes())
                .createdAt(now)
                .build();

        // Evaluar tardanza respecto al horario programado
        List<EmployeeSchedule> schedules = scheduleRepository.findActiveByEmployeeId(request.getEmployeeId());
        schedules.stream()
                .filter(s -> s.isActive() && s.getDayOfWeek() == today.getDayOfWeek().getValue())
                .findFirst()
                .ifPresent(schedule -> record.evaluateLate(schedule.getStartTime()));

        record.validate();
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
