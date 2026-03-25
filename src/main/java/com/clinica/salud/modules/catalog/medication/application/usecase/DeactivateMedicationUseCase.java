package com.clinica.salud.modules.catalog.medication.application.usecase;

import com.clinica.salud.modules.catalog.medication.domain.port.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeactivateMedicationUseCase {

    private final MedicationRepository medicationRepository;

    public void execute(UUID id) {
        medicationRepository.deactivate(id);
    }
}

