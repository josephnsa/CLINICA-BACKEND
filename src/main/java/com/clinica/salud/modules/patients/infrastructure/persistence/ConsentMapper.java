package com.clinica.salud.modules.patients.infrastructure.persistence;

import com.clinica.salud.modules.patients.domain.model.PatientConsent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConsentMapper {

    PatientConsent toDomain(ConsentEntity entity);

    ConsentEntity toEntity(PatientConsent domain);
}
