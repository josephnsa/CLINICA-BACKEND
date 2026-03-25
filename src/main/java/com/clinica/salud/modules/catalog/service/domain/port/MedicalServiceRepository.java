package com.clinica.salud.modules.catalog.service.domain.port;

import com.clinica.salud.modules.catalog.service.domain.model.MedicalService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicalServiceRepository {

    Optional<MedicalService> findByCode(String code);

    Optional<MedicalService> findById(UUID id);

    MedicalService save(MedicalService service);

    List<MedicalService> findActiveBySpecialty(UUID specialtyId);

    List<MedicalService> findAllActive();

    void deactivate(UUID id);
}

