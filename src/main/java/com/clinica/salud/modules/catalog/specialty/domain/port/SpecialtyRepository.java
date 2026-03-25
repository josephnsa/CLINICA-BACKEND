package com.clinica.salud.modules.catalog.specialty.domain.port;

import com.clinica.salud.modules.catalog.specialty.domain.model.Specialty;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecialtyRepository {

    Optional<Specialty> findByCode(String code);

    List<Specialty> findAllActive();

    Specialty save(Specialty specialty);

    void deactivate(UUID id);
}

