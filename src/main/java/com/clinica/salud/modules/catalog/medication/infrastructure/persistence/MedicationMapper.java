package com.clinica.salud.modules.catalog.medication.infrastructure.persistence;

import com.clinica.salud.modules.catalog.medication.domain.model.Medication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicationMapper {

    Medication toDomain(MedicationEntity entity);

    MedicationEntity toEntity(Medication domain);
}

