package com.clinica.salud.modules.catalog.medication.application.usecase;

import com.clinica.salud.modules.catalog.medication.application.dto.CreateMedicationRequest;
import com.clinica.salud.modules.catalog.medication.application.dto.MedicationResponse;
import com.clinica.salud.modules.catalog.medication.domain.model.Medication;
import com.clinica.salud.modules.catalog.medication.domain.port.MedicationRepository;
import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateMedicationUseCase {

    private final MedicationRepository medicationRepository;

    public MedicationResponse execute(CreateMedicationRequest request) {
        medicationRepository.findByCode(request.code())
                .ifPresent(m -> {
                    throw new BusinessRuleException("Ya existe un medicamento con código: " + request.code());
                });

        Medication medication = Medication.builder()
                .code(request.code())
                .genericName(request.genericName())
                .commercialName(request.commercialName())
                .presentation(request.presentation())
                .unit(request.unit())
                .isActive(true)
                .build();

        Medication saved = medicationRepository.save(medication);
        return new MedicationResponse(
                saved.getId(),
                saved.getCode(),
                saved.getGenericName(),
                saved.getCommercialName(),
                saved.getPresentation(),
                saved.getUnit(),
                saved.isActive()
        );
    }
}

