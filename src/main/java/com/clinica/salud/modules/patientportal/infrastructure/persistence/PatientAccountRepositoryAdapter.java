package com.clinica.salud.modules.patientportal.infrastructure.persistence;

import com.clinica.salud.modules.patientportal.domain.model.PatientAccount;
import com.clinica.salud.modules.patientportal.domain.port.PatientAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PatientAccountRepositoryAdapter implements PatientAccountRepository {

    private final PatientAccountJpaRepository jpaRepository;
    private final PatientAccountMapper mapper;

    @Override
    public PatientAccount save(PatientAccount account) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(account)));
    }

    @Override
    public Optional<PatientAccount> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<PatientAccount> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
