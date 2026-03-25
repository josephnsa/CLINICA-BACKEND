package com.clinica.salud.modules.agenda.infrastructure.persistence;

import com.clinica.salud.modules.agenda.domain.model.Triage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TriageMapper {

    Triage toDomain(TriageEntity entity);

    TriageEntity toEntity(Triage domain);
}
