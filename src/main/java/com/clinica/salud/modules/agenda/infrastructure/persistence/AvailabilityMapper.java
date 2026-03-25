package com.clinica.salud.modules.agenda.infrastructure.persistence;

import com.clinica.salud.modules.agenda.domain.model.AvailabilityBlock;
import com.clinica.salud.modules.agenda.domain.model.DoctorAvailabilityRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AvailabilityMapper {

    DoctorAvailabilityRule ruleToDomain(DoctorAvailabilityEntity entity);

    DoctorAvailabilityEntity ruleToEntity(DoctorAvailabilityRule domain);

    AvailabilityBlock blockToDomain(AvailabilityBlockEntity entity);

    AvailabilityBlockEntity blockToEntity(AvailabilityBlock domain);
}
