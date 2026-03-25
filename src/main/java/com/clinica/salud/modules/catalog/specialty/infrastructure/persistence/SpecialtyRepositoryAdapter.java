package com.clinica.salud.modules.catalog.specialty.infrastructure.persistence;

import com.clinica.salud.modules.catalog.specialty.domain.model.Specialty;
import com.clinica.salud.modules.catalog.specialty.domain.port.SpecialtyRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SpecialtyRepositoryAdapter implements SpecialtyRepository {

    private final SpecialtyJpaRepository specialtyJpaRepository;
    private final SpecialtyMapper specialtyMapper;

    @Override
    public Optional<Specialty> findByCode(String code) {
        return specialtyJpaRepository.findByCodeAndIsActiveTrue(code)
                .map(specialtyMapper::toDomain);
    }

    @Override
    public List<Specialty> findAllActive() {
        return specialtyJpaRepository.findByIsActiveTrue()
                .stream()
                .map(specialtyMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Specialty save(Specialty specialty) {
        SpecialtyEntity entity = specialtyMapper.toEntity(specialty);
        entity = specialtyJpaRepository.save(entity);
        return specialtyMapper.toDomain(entity);
    }

    @Override
    public void deactivate(UUID id) {
        SpecialtyEntity entity = specialtyJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", id.toString()));
        entity.setActive(false);
        specialtyJpaRepository.save(entity);
    }
}

