package com.clinica.salud.modules.catalog.service.application.usecase;

import com.clinica.salud.modules.catalog.service.application.dto.UpdateServiceRequest;
import com.clinica.salud.modules.catalog.service.application.dto.ServiceResponse;
import com.clinica.salud.modules.catalog.service.domain.model.MedicalService;
import com.clinica.salud.modules.catalog.service.domain.port.MedicalServiceRepository;
import com.clinica.salud.modules.catalog.specialty.infrastructure.persistence.SpecialtyEntity;
import com.clinica.salud.modules.catalog.specialty.infrastructure.persistence.SpecialtyJpaRepository;
import com.clinica.salud.shared.exception.BusinessRuleException;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateServiceUseCase {

    private final MedicalServiceRepository medicalServiceRepository;
    private final SpecialtyJpaRepository specialtyJpaRepository;

    public ServiceResponse execute(UUID id, UpdateServiceRequest request) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id.toString()));

        String code = request.code().trim();
        medicalServiceRepository.findByCode(code)
                .filter(s -> !s.getId().equals(id))
                .ifPresent(s -> {
                    throw new BusinessRuleException("Ya existe un servicio con código: " + code);
                });

        UUID specialtyId = request.specialtyId();
        if (specialtyId != null) {
            specialtyJpaRepository.findById(specialtyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty", specialtyId.toString()));
        }

        service.setCode(code);
        service.setName(request.name().trim());
        service.setSpecialtyId(specialtyId);
        service.setDurationMin(request.durationMin());
        service.setPrice(request.price());
        service.setActive(request.isActive());

        MedicalService saved = medicalServiceRepository.save(service);

        String specialtyName = null;
        if (saved.getSpecialtyId() != null) {
            specialtyName = specialtyJpaRepository.findById(saved.getSpecialtyId())
                    .map(SpecialtyEntity::getName)
                    .orElse(null);
        }

        return new ServiceResponse(
                saved.getId(),
                saved.getCode(),
                saved.getName(),
                saved.getSpecialtyId(),
                specialtyName,
                saved.getDurationMin(),
                saved.getPrice(),
                saved.isActive()
        );
    }
}

