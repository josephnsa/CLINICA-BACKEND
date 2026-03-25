package com.clinica.salud.modules.catalog.medication.domain.port;

import com.clinica.salud.modules.catalog.medication.domain.model.Medication;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicationRepository {

    Optional<Medication> findByCode(String code);

    Optional<Medication> findById(UUID id);

    Medication save(Medication medication);

    void deactivate(UUID id);

    Page<Medication> searchActive(String query, Pageable pageable);
}

