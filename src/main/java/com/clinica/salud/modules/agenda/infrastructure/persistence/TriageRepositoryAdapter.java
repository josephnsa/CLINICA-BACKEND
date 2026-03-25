package com.clinica.salud.modules.agenda.infrastructure.persistence;

import com.clinica.salud.modules.agenda.domain.model.Triage;
import com.clinica.salud.modules.agenda.domain.port.TriageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TriageRepositoryAdapter implements TriageRepository {

    private final TriageJpaRepository jpaRepository;
    private final TriageMapper mapper;

    @Override
    public Triage save(Triage triage) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(triage)));
    }

    @Override
    public Optional<Triage> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Triage> findByAppointmentId(UUID appointmentId) {
        return jpaRepository.findByAppointmentId(appointmentId).map(mapper::toDomain);
    }

    @Override
    public List<Triage> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientIdOrderByRecordedAtDesc(patientId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
