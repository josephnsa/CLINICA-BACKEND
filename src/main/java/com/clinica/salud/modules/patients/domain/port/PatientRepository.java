package com.clinica.salud.modules.patients.domain.port;

import com.clinica.salud.modules.patients.domain.model.Patient;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientRepository {

    Patient save(Patient patient);

    Optional<Patient> findById(UUID id);

    Optional<Patient> findByDocNumber(String docNumber);

    boolean existsByDocNumber(String docNumber);

    Page<Patient> search(String query, String docNumber, boolean activeOnly, Pageable pageable);
}
