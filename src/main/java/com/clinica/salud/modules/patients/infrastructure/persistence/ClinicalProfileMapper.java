package com.clinica.salud.modules.patients.infrastructure.persistence;

import com.clinica.salud.modules.patients.domain.model.ClinicalProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClinicalProfileMapper {

    ClinicalProfile toDomain(ClinicalProfileEntity entity);

    ClinicalProfileEntity toEntity(ClinicalProfile domain);
}
