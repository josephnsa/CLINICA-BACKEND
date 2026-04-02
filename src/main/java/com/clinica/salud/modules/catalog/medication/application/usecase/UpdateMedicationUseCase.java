package com.clinica.salud.modules.catalog.medication.application.usecase;

import com.clinica.salud.modules.catalog.medication.application.dto.MedicationResponse;
import com.clinica.salud.modules.catalog.medication.application.dto.UpdateMedicationRequest;
import com.clinica.salud.modules.catalog.medication.domain.model.Medication;
import com.clinica.salud.modules.catalog.medication.domain.port.MedicationRepository;
import com.clinica.salud.shared.exception.BusinessRuleException;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateMedicationUseCase {

    private final MedicationRepository medicationRepository;

    public MedicationResponse execute(UUID id, UpdateMedicationRequest request) {
        Medication med = medicationRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication", id.toString()));

        if (!med.getCode().equalsIgnoreCase(request.code())) {
            medicationRepository
                    .findByCode(request.code())
                    .filter(other -> !other.getId().equals(id))
                    .ifPresent(
                            other -> {
                                throw new BusinessRuleException(
                                        "Ya existe un medicamento con código: " + request.code());
                            });
        }

        med.setCode(request.code());
        med.setGenericName(request.genericName());
        med.setCommercialName(request.commercialName());
        med.setPresentation(request.presentation());
        med.setUnit(request.unit());
        med.setActive(request.active());

        Medication saved = medicationRepository.save(med);
        return new MedicationResponse(
                saved.getId(),
                saved.getCode(),
                saved.getGenericName(),
                saved.getCommercialName(),
                saved.getPresentation(),
                saved.getUnit(),
                saved.isActive());
    }
}
