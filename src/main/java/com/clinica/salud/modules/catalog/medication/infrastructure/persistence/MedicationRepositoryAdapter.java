package com.clinica.salud.modules.catalog.medication.infrastructure.persistence;

import com.clinica.salud.modules.catalog.medication.domain.model.Medication;
import com.clinica.salud.modules.catalog.medication.domain.port.MedicationRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MedicationRepositoryAdapter implements MedicationRepository {

    private final MedicationJpaRepository medicationJpaRepository;
    private final MedicationMapper medicationMapper;

    @Override
    public Optional<Medication> findByCode(String code) {
        return medicationJpaRepository.findByCode(code).map(medicationMapper::toDomain);
    }

    @Override
    public Optional<Medication> findById(UUID id) {
        return medicationJpaRepository.findById(id).map(medicationMapper::toDomain);
    }

    @Override
    public Medication save(Medication medication) {
        MedicationEntity entity = medicationMapper.toEntity(medication);
        entity = medicationJpaRepository.save(entity);
        return medicationMapper.toDomain(entity);
    }

    @Override
    public void deactivate(UUID id) {
        MedicationEntity entity = medicationJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication", id.toString()));
        entity.setActive(false);
        medicationJpaRepository.save(entity);
    }

    @Override
    public Page<Medication> search(String q, boolean activeOnly, Pageable pageable) {
        String term = q != null ? q.trim() : "";
        if (term.isEmpty()) {
            if (activeOnly) {
                return medicationJpaRepository.findByIsActiveTrue(pageable).map(medicationMapper::toDomain);
            }
            return medicationJpaRepository.findAll(pageable).map(medicationMapper::toDomain);
        }
        return medicationJpaRepository.searchByTerm(term, activeOnly, pageable).map(medicationMapper::toDomain);
    }
}

