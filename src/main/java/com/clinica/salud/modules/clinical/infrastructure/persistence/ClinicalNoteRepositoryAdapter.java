package com.clinica.salud.modules.clinical.infrastructure.persistence;

import com.clinica.salud.modules.clinical.domain.model.ClinicalNote;
import com.clinica.salud.modules.clinical.domain.port.ClinicalNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ClinicalNoteRepositoryAdapter implements ClinicalNoteRepository {

    private final ClinicalNoteJpaRepository jpaRepository;
    private final ClinicalNoteMapper mapper;

    @Override
    public ClinicalNote save(ClinicalNote note) {
        ClinicalNoteEntity entity = mapper.toEntity(note);
        entity = jpaRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Page<ClinicalNote> findByPatientIdOrderByCreatedAtDesc(UUID patientId, Pageable pageable) {
        return jpaRepository.findByPatientIdOrderByCreatedAtDesc(patientId, pageable).map(mapper::toDomain);
    }
}
