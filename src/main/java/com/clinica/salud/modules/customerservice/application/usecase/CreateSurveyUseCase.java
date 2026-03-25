package com.clinica.salud.modules.customerservice.application.usecase;

import com.clinica.salud.modules.customerservice.application.dto.CreateSurveyRequest;
import com.clinica.salud.modules.customerservice.application.dto.SurveyResponse;
import com.clinica.salud.modules.customerservice.domain.model.SatisfactionSurvey;
import com.clinica.salud.modules.customerservice.domain.port.SatisfactionSurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateSurveyUseCase {

    private final SatisfactionSurveyRepository surveyRepository;

    @Transactional
    public SurveyResponse execute(CreateSurveyRequest request) {
        SatisfactionSurvey survey = new SatisfactionSurvey();
        survey.setPatientId(request.getPatientId());
        survey.setAppointmentId(request.getAppointmentId());
        survey.rate(request.getScore(), request.getComment());

        SatisfactionSurvey saved = surveyRepository.save(survey);
        return toResponse(saved);
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
