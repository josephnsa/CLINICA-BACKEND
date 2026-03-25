package com.clinica.salud.modules.clinical.domain.port;

import com.clinica.salud.modules.clinical.domain.model.ClinicalNote;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClinicalNoteRepository {

    ClinicalNote save(ClinicalNote note);

    Page<ClinicalNote> findByPatientIdOrderByCreatedAtDesc(UUID patientId, Pageable pageable);
}
