package com.clinica.salud.modules.hrm.infrastructure.persistence;

import com.clinica.salud.modules.hrm.domain.model.AttendanceRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    AttendanceEntity toEntity(AttendanceRecord domain);

    AttendanceRecord toDomain(AttendanceEntity entity);
}
