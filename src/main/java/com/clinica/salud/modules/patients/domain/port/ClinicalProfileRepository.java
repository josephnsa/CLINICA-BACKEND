package com.clinica.salud.modules.patients.domain.port;

import com.clinica.salud.modules.patients.domain.model.ClinicalProfile;

import java.util.Optional;
import java.util.UUID;

public interface ClinicalProfileRepository {

    ClinicalProfile save(ClinicalProfile profile);

    Optional<ClinicalProfile> findByPatientId(UUID patientId);
}
