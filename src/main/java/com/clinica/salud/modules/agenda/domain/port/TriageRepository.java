package com.clinica.salud.modules.agenda.domain.port;

import com.clinica.salud.modules.agenda.domain.model.Triage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TriageRepository {

    Triage save(Triage triage);

    Optional<Triage> findById(UUID id);

    Optional<Triage> findByAppointmentId(UUID appointmentId);

    List<Triage> findByPatientId(UUID patientId);
}
