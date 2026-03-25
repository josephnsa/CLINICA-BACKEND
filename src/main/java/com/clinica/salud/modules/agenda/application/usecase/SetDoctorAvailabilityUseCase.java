package com.clinica.salud.modules.agenda.application.usecase;

import com.clinica.salud.modules.agenda.application.dto.AvailabilityRuleResponse;
import com.clinica.salud.modules.agenda.application.dto.SetAvailabilityRuleRequest;
import com.clinica.salud.modules.agenda.domain.model.DoctorAvailabilityRule;
import com.clinica.salud.modules.agenda.domain.port.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SetDoctorAvailabilityUseCase {

    private final AvailabilityRepository availabilityRepository;

    private static final String[] DAY_NAMES = {"", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

    @Transactional
    public AvailabilityRuleResponse execute(SetAvailabilityRuleRequest request) {
        DoctorAvailabilityRule rule = DoctorAvailabilityRule.builder()
                .doctorId(request.doctorId())
                .sedeId(request.sedeId())
                .dayOfWeek(request.dayOfWeek())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .isActive(true)
                .build();
        rule.validate();
        DoctorAvailabilityRule saved = availabilityRepository.saveRule(rule);
        return toResponse(saved);
    }

    public List<AvailabilityRuleResponse> getRulesByDoctor(java.util.UUID doctorId) {
        return availabilityRepository.findRulesByDoctor(doctorId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deactivateRule(java.util.UUID ruleId) {
        availabilityRepository.deactivateRule(ruleId);
    }

    private AvailabilityRuleResponse toResponse(DoctorAvailabilityRule rule) {
        return new AvailabilityRuleResponse(
                rule.getId(),
                rule.getDoctorId(),
                rule.getSedeId(),
                rule.getDayOfWeek(),
                DAY_NAMES[rule.getDayOfWeek()],
                rule.getStartTime(),
                rule.getEndTime(),
                rule.isActive()
        );
    }
}
