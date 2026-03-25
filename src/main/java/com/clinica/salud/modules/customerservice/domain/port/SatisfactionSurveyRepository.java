package com.clinica.salud.modules.customerservice.domain.port;

import com.clinica.salud.modules.customerservice.domain.model.SatisfactionSurvey;

import java.util.List;
import java.util.UUID;

public interface SatisfactionSurveyRepository {

    SatisfactionSurvey save(SatisfactionSurvey survey);

    List<SatisfactionSurvey> findByPatientId(UUID patientId);

    List<SatisfactionSurvey> findByAppointmentId(UUID appointmentId);
}
