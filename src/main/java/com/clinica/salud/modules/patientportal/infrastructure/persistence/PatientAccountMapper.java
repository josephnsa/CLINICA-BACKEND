package com.clinica.salud.modules.patientportal.infrastructure.persistence;

import com.clinica.salud.modules.patientportal.domain.model.PatientAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientAccountMapper {

    PatientAccountEntity toEntity(PatientAccount domain);

    PatientAccount toDomain(PatientAccountEntity entity);
}
