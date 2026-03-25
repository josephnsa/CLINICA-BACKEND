package com.clinica.salud.modules.catalog.specialty.infrastructure.persistence;

import com.clinica.salud.modules.catalog.specialty.domain.model.Specialty;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    Specialty toDomain(SpecialtyEntity entity);

    SpecialtyEntity toEntity(Specialty domain);
}

