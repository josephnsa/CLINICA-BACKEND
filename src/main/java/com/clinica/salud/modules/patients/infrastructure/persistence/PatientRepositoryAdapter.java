package com.clinica.salud.modules.patients.infrastructure.persistence;

import com.clinica.salud.modules.patients.domain.model.Patient;
import com.clinica.salud.modules.patients.domain.port.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PatientRepositoryAdapter implements PatientRepository {

    private final PatientJpaRepository jpaRepository;
    private final PatientMapper mapper;

    @Override
    public Patient save(Patient patient) {
        PatientEntity entity = mapper.toEntity(patient);
        entity = jpaRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Patient> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Patient> findByDocNumber(String docNumber) {
        return jpaRepository.findByDocNumber(docNumber).map(mapper::toDomain);
    }

    @Override
    public boolean existsByDocNumber(String docNumber) {
        return jpaRepository.existsByDocNumber(docNumber);
    }

    @Override
    public Page<Patient> search(String query, String docNumber, boolean activeOnly, Pageable pageable) {
        return jpaRepository.search(query, docNumber, activeOnly, pageable).map(mapper::toDomain);
    }
}
