package com.clinica.salud.modules.catalog.specialty.application.usecase;

import com.clinica.salud.modules.catalog.specialty.application.dto.SpecialtyResponse;
import com.clinica.salud.modules.catalog.specialty.domain.port.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListSpecialtiesUseCase {

    private final SpecialtyRepository specialtyRepository;

    public List<SpecialtyResponse> execute() {
        return specialtyRepository.findAllActive().stream()
                .map(s -> new SpecialtyResponse(s.getId(), s.getCode(), s.getName(), s.isActive()))
                .collect(Collectors.toList());
    }
}

