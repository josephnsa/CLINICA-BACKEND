package com.clinica.salud.modules.catalog.specialty.application.usecase;

import com.clinica.salud.modules.catalog.specialty.domain.port.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeactivateSpecialtyUseCase {

    private final SpecialtyRepository specialtyRepository;

    public void execute(UUID id) {
        specialtyRepository.deactivate(id);
    }
}

