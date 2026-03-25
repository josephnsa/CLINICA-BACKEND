package com.clinica.salud.modules.catalog.service.application.usecase;

import com.clinica.salud.modules.catalog.service.domain.port.MedicalServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeactivateServiceUseCase {

    private final MedicalServiceRepository medicalServiceRepository;

    public void execute(UUID id) {
        medicalServiceRepository.deactivate(id);
    }
}

