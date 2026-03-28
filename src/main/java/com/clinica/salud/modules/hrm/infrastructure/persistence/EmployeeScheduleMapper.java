package com.clinica.salud.modules.hrm.infrastructure.persistence;

import com.clinica.salud.modules.hrm.domain.model.EmployeeSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeScheduleMapper {

    EmployeeSchedule toDomain(EmployeeScheduleEntity entity);

    EmployeeScheduleEntity toEntity(EmployeeSchedule domain);
}
