package com.clinica.salud.modules.catalog.service.infrastructure.persistence;

import com.clinica.salud.modules.catalog.service.domain.model.MedicalService;
import com.clinica.salud.modules.catalog.service.domain.port.MedicalServiceRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MedicalServiceRepositoryAdapter implements MedicalServiceRepository {

    private final MedicalServiceJpaRepository medicalServiceJpaRepository;
    private final ServiceMapper serviceMapper;

    @Override
    public Optional<MedicalService> findByCode(String code) {
        return medicalServiceJpaRepository.findByCode(code).map(serviceMapper::toDomain);
    }

    @Override
    public Optional<MedicalService> findById(UUID id) {
        return medicalServiceJpaRepository.findById(id).map(serviceMapper::toDomain);
    }

    @Override
    public MedicalService save(MedicalService service) {
        ServiceEntity entity = serviceMapper.toEntity(service);
        entity = medicalServiceJpaRepository.save(entity);
        return serviceMapper.toDomain(entity);
    }

    @Override
    public List<MedicalService> findActiveBySpecialty(UUID specialtyId) {
        return medicalServiceJpaRepository.findBySpecialty_IdAndIsActiveTrue(specialtyId)
                .stream()
                .map(serviceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalService> findAllActive() {
        return medicalServiceJpaRepository.findByIsActiveTrue()
                .stream()
                .map(serviceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deactivate(UUID id) {
        ServiceEntity entity = medicalServiceJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id.toString()));
        entity.setActive(false);
        medicalServiceJpaRepository.save(entity);
    }
}

