package com.clinica.salud.modules.patients.infrastructure.persistence;

import com.clinica.salud.modules.patients.domain.model.PatientConsent;
import com.clinica.salud.modules.patients.domain.port.ConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConsentRepositoryAdapter implements ConsentRepository {

    private final ConsentJpaRepository jpaRepository;
    private final ConsentMapper mapper;

    @Override
    public PatientConsent save(PatientConsent consent) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(consent)));
    }

    @Override
    public Optional<PatientConsent> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PatientConsent> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientIdOrderBySignedAtDesc(patientId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}
