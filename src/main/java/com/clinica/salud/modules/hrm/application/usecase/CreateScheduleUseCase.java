package com.clinica.salud.modules.hrm.application.usecase;

import com.clinica.salud.modules.hrm.application.dto.CreateScheduleRequest;
import com.clinica.salud.modules.hrm.application.dto.ScheduleResponse;
import com.clinica.salud.modules.hrm.domain.model.Employee;
import com.clinica.salud.modules.hrm.domain.model.EmployeeSchedule;
import com.clinica.salud.modules.hrm.domain.port.EmployeeRepository;
import com.clinica.salud.modules.hrm.domain.port.ScheduleRepository;
import com.clinica.salud.shared.exception.BusinessRuleException;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateScheduleUseCase {

    private final ScheduleRepository scheduleRepository;
    private final EmployeeRepository employeeRepository;

    public ScheduleResponse execute(CreateScheduleRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", request.getEmployeeId().toString()));

        if (!employee.isActive()) {
            throw new BusinessRuleException("No se puede asignar horario a un empleado inactivo");
        }

        if (scheduleRepository.existsActiveForDay(request.getEmployeeId(), request.getDayOfWeek())) {
            throw new BusinessRuleException("El empleado ya tiene un horario activo para ese día de la semana");
        }

        EmployeeSchedule schedule = EmployeeSchedule.builder()
                .employeeId(request.getEmployeeId())
                .sedeId(request.getSedeId())
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(true)
                .build();

        schedule.validate();

        EmployeeSchedule saved = scheduleRepository.save(schedule);
        return toResponse(saved);
    }

    static ScheduleResponse toResponse(EmployeeSchedule s) {
        return ScheduleResponse.builder()
                .id(s.getId())
                .employeeId(s.getEmployeeId())
                .sedeId(s.getSedeId())
                .dayOfWeek(s.getDayOfWeek())
                .dayName(s.getDayName())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .durationMinutes(s.getDurationMinutes())
                .active(s.isActive())
                .build();
    }
}
