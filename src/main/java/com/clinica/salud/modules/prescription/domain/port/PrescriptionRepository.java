package com.clinica.salud.modules.prescription.domain.port;

import com.clinica.salud.modules.prescription.domain.model.Prescription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrescriptionRepository {

    Prescription save(Prescription prescription);

    Optional<Prescription> findById(UUID id);

    List<Prescription> findByPatient(UUID patientId);
}

