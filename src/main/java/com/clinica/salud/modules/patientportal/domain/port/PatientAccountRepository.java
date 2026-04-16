package com.clinica.salud.modules.patientportal.domain.port;

import com.clinica.salud.modules.patientportal.domain.model.PatientAccount;

import java.util.Optional;
import java.util.UUID;

public interface PatientAccountRepository {

    PatientAccount save(PatientAccount account);

    Optional<PatientAccount> findByEmail(String email);

    Optional<PatientAccount> findByPatientId(UUID patientId);

    boolean existsByEmail(String email);
}
