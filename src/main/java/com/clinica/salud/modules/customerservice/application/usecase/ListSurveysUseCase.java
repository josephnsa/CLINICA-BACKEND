package com.clinica.salud.modules.customerservice.application.usecase;

import com.clinica.salud.modules.customerservice.application.dto.SurveyResponse;
import com.clinica.salud.modules.customerservice.domain.model.SatisfactionSurvey;
import com.clinica.salud.modules.customerservice.domain.port.SatisfactionSurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListSurveysUseCase {

    private final SatisfactionSurveyRepository surveyRepository;

    @Transactional(readOnly = true)
    public List<SurveyResponse> execute(UUID patientId, UUID appointmentId) {
        List<SatisfactionSurvey> surveys;

        if (patientId != null) {
            surveys = surveyRepository.findByPatientId(patientId);
        } else if (appointmentId != null) {
            surveys = surveyRepository.findByAppointmentId(appointmentId);
        } else {
            surveys = surveyRepository.findAll();
        }

        return surveys.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private SurveyResponse toResponse(SatisfactionSurvey s) {
        return SurveyResponse.builder()
                .id(s.getId())
                .patientId(s.getPatientId())
                .appointmentId(s.getAppointmentId())
                .score(s.getScore())
                .scoreLabel(s.getScoreLabel())
                .comment(s.getComment())
                .positive(s.isPositive())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
