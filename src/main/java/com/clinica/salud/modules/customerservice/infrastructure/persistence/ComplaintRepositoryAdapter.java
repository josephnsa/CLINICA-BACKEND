package com.clinica.salud.modules.customerservice.infrastructure.persistence;

import com.clinica.salud.modules.customerservice.domain.model.Complaint;
import com.clinica.salud.modules.customerservice.domain.model.ComplaintStatus;
import com.clinica.salud.modules.customerservice.domain.port.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ComplaintRepositoryAdapter implements ComplaintRepository {

    private final ComplaintJpaRepository jpaRepository;
    private final ComplaintMapper mapper;

    @Override
    public Complaint save(Complaint complaint) {
        ComplaintEntity entity = mapper.toEntity(complaint);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Complaint> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Complaint> findBySedeId(UUID sedeId) {
        return jpaRepository.findBySedeId(sedeId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByStatus(ComplaintStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }
}
