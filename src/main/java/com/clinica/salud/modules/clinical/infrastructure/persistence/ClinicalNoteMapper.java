package com.clinica.salud.modules.clinical.infrastructure.persistence;

import com.clinica.salud.modules.clinical.domain.model.ClinicalNote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClinicalNoteMapper {

    ClinicalNote toDomain(ClinicalNoteEntity entity);

    ClinicalNoteEntity toEntity(ClinicalNote domain);
}
