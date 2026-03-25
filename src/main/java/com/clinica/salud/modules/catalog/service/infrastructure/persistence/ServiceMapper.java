package com.clinica.salud.modules.catalog.service.infrastructure.persistence;

import com.clinica.salud.modules.catalog.service.domain.model.MedicalService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(target = "specialtyId", source = "specialty.id")
    MedicalService toDomain(ServiceEntity entity);

    @Mapping(target = "specialty.id", source = "specialtyId")
    @Mapping(target = "specialty.code", ignore = true)
    @Mapping(target = "specialty.name", ignore = true)
    @Mapping(target = "specialty.active", ignore = true)
    ServiceEntity toEntity(MedicalService domain);
}

