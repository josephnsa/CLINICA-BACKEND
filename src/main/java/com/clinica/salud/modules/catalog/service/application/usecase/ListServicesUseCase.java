package com.clinica.salud.modules.catalog.service.application.usecase;

import com.clinica.salud.modules.catalog.service.application.dto.ServiceFilterRequest;
import com.clinica.salud.modules.catalog.service.application.dto.ServiceResponse;
import com.clinica.salud.modules.catalog.service.domain.model.MedicalService;
import com.clinica.salud.modules.catalog.service.domain.port.MedicalServiceRepository;
import com.clinica.salud.modules.catalog.specialty.infrastructure.persistence.SpecialtyEntity;
import com.clinica.salud.modules.catalog.specialty.infrastructure.persistence.SpecialtyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListServicesUseCase {

    private final MedicalServiceRepository medicalServiceRepository;
    private final SpecialtyJpaRepository specialtyJpaRepository;

    public List<ServiceResponse> execute(ServiceFilterRequest filter) {
        List<MedicalService> services;
        if (filter.specialtyId() != null) {
            services = filter.activeOnly()
                    ? medicalServiceRepository.findActiveBySpecialty(filter.specialtyId())
                    : medicalServiceRepository.findBySpecialty(filter.specialtyId());
        } else {
            services = filter.activeOnly()
                    ? medicalServiceRepository.findAllActive()
                    : medicalServiceRepository.findAllServices();
        }

        Map<UUID, String> specialtyNames = specialtyJpaRepository.findAll().stream()
                .collect(Collectors.toMap(SpecialtyEntity::getId, SpecialtyEntity::getName));

        return services.stream()
                .map(s -> new ServiceResponse(
                        s.getId(),
                        s.getCode(),
                        s.getName(),
                        s.getSpecialtyId(),
                        specialtyNames.getOrDefault(s.getSpecialtyId(), null),
                        s.getDurationMin(),
                        s.getPrice(),
                        s.isActive()
                ))
                .collect(Collectors.toList());
    }
}

