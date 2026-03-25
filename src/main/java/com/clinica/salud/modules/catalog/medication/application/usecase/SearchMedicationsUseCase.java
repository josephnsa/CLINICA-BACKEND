package com.clinica.salud.modules.catalog.medication.application.usecase;

import com.clinica.salud.modules.catalog.medication.application.dto.MedicationResponse;
import com.clinica.salud.modules.catalog.medication.domain.model.Medication;
import com.clinica.salud.modules.catalog.medication.domain.port.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchMedicationsUseCase {

    private final MedicationRepository medicationRepository;

    public Page<MedicationResponse> execute(String query, Pageable pageable) {
        Page<Medication> page = medicationRepository.searchActive(
                query != null ? query.trim() : "",
                pageable
        );
        return page.map(m -> new MedicationResponse(
                m.getId(),
                m.getCode(),
                m.getGenericName(),
                m.getCommercialName(),
                m.getPresentation(),
                m.getUnit(),
                m.isActive()
        ));
    }
}

