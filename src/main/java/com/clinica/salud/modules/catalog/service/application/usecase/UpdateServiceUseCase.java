package com.clinica.salud.modules.catalog.service.application.usecase;

import com.clinica.salud.modules.catalog.service.application.dto.ServiceResponse;
import com.clinica.salud.modules.catalog.service.domain.model.MedicalService;
import com.clinica.salud.modules.catalog.service.domain.port.MedicalServiceRepository;
import com.clinica.salud.modules.catalog.specialty.infrastructure.persistence.SpecialtyEntity;
import com.clinica.salud.modules.catalog.specialty.infrastructure.persistence.SpecialtyJpaRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateServiceUseCase {

    private final MedicalServiceRepository medicalServiceRepository;
    private final SpecialtyJpaRepository specialtyJpaRepository;

    public ServiceResponse execute(UUID id, Integer durationMin, BigDecimal price) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id.toString()));

        if (durationMin != null && durationMin > 0) {
            service.setDurationMin(durationMin);
        }
        if (price != null && price.compareTo(BigDecimal.ZERO) >= 0) {
            service.setPrice(price);
        }

        MedicalService saved = medicalServiceRepository.save(service);
        SpecialtyEntity specialty = specialtyJpaRepository.findById(saved.getSpecialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", saved.getSpecialtyId().toString()));

        return new ServiceResponse(
                saved.getId(),
                saved.getCode(),
                saved.getName(),
                saved.getSpecialtyId(),
                specialty.getName(),
                saved.getDurationMin(),
                saved.getPrice(),
                saved.isActive()
        );
    }
}

