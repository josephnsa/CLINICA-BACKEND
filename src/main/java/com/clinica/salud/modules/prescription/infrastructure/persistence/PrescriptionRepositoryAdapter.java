package com.clinica.salud.modules.prescription.infrastructure.persistence;

import com.clinica.salud.modules.prescription.domain.model.Prescription;
import com.clinica.salud.modules.prescription.domain.port.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PrescriptionRepositoryAdapter implements PrescriptionRepository {

    private final PrescriptionJpaRepository jpaRepository;
    private final PrescriptionMapper mapper;

    @Override
    public Prescription save(Prescription prescription) {
        PrescriptionEntity entity = mapper.toEntity(prescription);
        PrescriptionEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Prescription> findById(UUID id) {
        return jpaRepository.findWithItemsById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Prescription> findByPatient(UUID patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}

