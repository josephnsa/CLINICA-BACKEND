package com.clinica.salud.modules.catalog.specialty.application.usecase;

import com.clinica.salud.modules.catalog.specialty.application.dto.SpecialtyResponse;
import com.clinica.salud.modules.catalog.specialty.domain.model.Specialty;
import com.clinica.salud.modules.catalog.specialty.domain.port.SpecialtyRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateSpecialtyUseCase {

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyResponse execute(UUID id, String name) {
        Specialty specialty = specialtyRepository.findAllActive().stream()
                .filter(s -> id.equals(s.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", id.toString()));

        specialty.setName(name);
        Specialty saved = specialtyRepository.save(specialty);
        return new SpecialtyResponse(saved.getId(), saved.getCode(), saved.getName(), saved.isActive());
    }
}

