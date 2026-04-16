package com.clinica.salud.modules.customerservice.infrastructure.persistence;

import com.clinica.salud.modules.customerservice.domain.model.SatisfactionSurvey;
import com.clinica.salud.modules.customerservice.domain.port.SatisfactionSurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SatisfactionSurveyRepositoryAdapter implements SatisfactionSurveyRepository {

    private final SatisfactionSurveyJpaRepository jpaRepository;
    private final SatisfactionSurveyMapper mapper;

    @Override
    public SatisfactionSurvey save(SatisfactionSurvey survey) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(survey)));
    }

    @Override
    public List<SatisfactionSurvey> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<SatisfactionSurvey> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<SatisfactionSurvey> findByAppointmentId(UUID appointmentId) {
        return jpaRepository.findByAppointmentId(appointmentId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }
}
