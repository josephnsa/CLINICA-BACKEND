package com.clinica.salud.modules.catalog.service.application.usecase;

import com.clinica.salud.modules.catalog.service.application.dto.CreateServiceRequest;
import com.clinica.salud.modules.catalog.service.application.dto.ServiceResponse;
import com.clinica.salud.modules.catalog.service.domain.model.MedicalService;
import com.clinica.salud.modules.catalog.service.domain.port.MedicalServiceRepository;
import com.clinica.salud.modules.catalog.specialty.infrastructure.persistence.SpecialtyEntity;
import com.clinica.salud.modules.catalog.specialty.infrastructure.persistence.SpecialtyJpaRepository;
import com.clinica.salud.shared.exception.BusinessRuleException;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateServiceUseCase {

    private final MedicalServiceRepository medicalServiceRepository;
    private final SpecialtyJpaRepository specialtyJpaRepository;

    public ServiceResponse execute(CreateServiceRequest request) {
        medicalServiceRepository.findByCode(request.code())
                .ifPresent(s -> {
                    throw new BusinessRuleException("Ya existe un servicio con código: " + request.code());
                });

        SpecialtyEntity specialty = specialtyJpaRepository.findById(request.specialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", request.specialtyId().toString()));

        MedicalService service = MedicalService.builder()
                .code(request.code())
                .name(request.name())
                .specialtyId(request.specialtyId())
                .durationMin(request.durationMin())
                .price(request.price())
                .isActive(true)
                .build();

        MedicalService saved = medicalServiceRepository.save(service);
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

