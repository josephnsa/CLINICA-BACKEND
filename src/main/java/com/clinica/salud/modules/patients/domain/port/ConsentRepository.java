package com.clinica.salud.modules.patients.domain.port;

import com.clinica.salud.modules.patients.domain.model.PatientConsent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsentRepository {

    PatientConsent save(PatientConsent consent);

    Optional<PatientConsent> findById(UUID id);

    List<PatientConsent> findByPatientId(UUID patientId);

    void delete(UUID id);
}
