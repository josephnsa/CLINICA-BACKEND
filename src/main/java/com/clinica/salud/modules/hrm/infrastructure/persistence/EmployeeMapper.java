package com.clinica.salud.modules.hrm.infrastructure.persistence;

import com.clinica.salud.modules.hrm.domain.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeMapper {

    Employee toDomain(EmployeeEntity entity);

    EmployeeEntity toEntity(Employee domain);
}
