package com.clinica.salud.modules.catalog.medication.infrastructure.persistence;

import com.clinica.salud.modules.catalog.medication.domain.model.Medication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MedicationMapper {

    @Mapping(target = "isActive", expression = "java(entity.isActive())")
    Medication toDomain(MedicationEntity entity);

    MedicationEntity toEntity(Medication domain);
}

