package com.clinica.salud.modules.patients.infrastructure.persistence;

import com.clinica.salud.modules.patients.domain.model.ClinicalProfile;
import com.clinica.salud.modules.patients.domain.port.ClinicalProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ClinicalProfileRepositoryAdapter implements ClinicalProfileRepository {

    private final ClinicalProfileJpaRepository jpaRepository;
    private final ClinicalProfileMapper mapper;

    @Override
    public ClinicalProfile save(ClinicalProfile profile) {
        ClinicalProfileEntity entity = mapper.toEntity(profile);
        entity = jpaRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<ClinicalProfile> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId).map(mapper::toDomain);
    }
}
