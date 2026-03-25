package com.clinica.salud.modules.catalog.specialty.application.usecase;

import com.clinica.salud.modules.catalog.specialty.application.dto.CreateSpecialtyRequest;
import com.clinica.salud.modules.catalog.specialty.application.dto.SpecialtyResponse;
import com.clinica.salud.modules.catalog.specialty.domain.model.Specialty;
import com.clinica.salud.modules.catalog.specialty.domain.port.SpecialtyRepository;
import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateSpecialtyUseCase {

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyResponse execute(CreateSpecialtyRequest request) {
        specialtyRepository.findByCode(request.code())
                .ifPresent(s -> {
                    throw new BusinessRuleException("Ya existe una especialidad con código: " + request.code());
                });

        Specialty specialty = Specialty.builder()
                .code(request.code())
                .name(request.name())
                .isActive(true)
                .build();

        Specialty saved = specialtyRepository.save(specialty);
        return new SpecialtyResponse(saved.getId(), saved.getCode(), saved.getName(), saved.isActive());
    }
}

