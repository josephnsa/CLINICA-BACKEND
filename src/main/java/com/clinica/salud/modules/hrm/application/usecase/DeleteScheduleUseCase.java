package com.clinica.salud.modules.hrm.application.usecase;

import com.clinica.salud.modules.hrm.domain.port.ScheduleRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteScheduleUseCase {

    private final ScheduleRepository scheduleRepository;

    public void execute(UUID scheduleId) {
        scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Horario", scheduleId.toString()));
        scheduleRepository.deactivate(scheduleId);
    }
}
