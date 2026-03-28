package com.clinica.salud.modules.hrm.application.usecase;

import com.clinica.salud.modules.hrm.application.dto.ScheduleResponse;
import com.clinica.salud.modules.hrm.domain.port.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListSchedulesUseCase {

    private final ScheduleRepository scheduleRepository;

    public List<ScheduleResponse> execute(UUID employeeId, boolean activeOnly) {
        List<com.clinica.salud.modules.hrm.domain.model.EmployeeSchedule> schedules = activeOnly
                ? scheduleRepository.findActiveByEmployeeId(employeeId)
                : scheduleRepository.findAllByEmployeeId(employeeId);

        return schedules.stream()
                .sorted(Comparator.comparingInt(s -> s.getDayOfWeek()))
                .map(CreateScheduleUseCase::toResponse)
                .collect(Collectors.toList());
    }
}
