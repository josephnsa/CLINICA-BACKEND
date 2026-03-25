package com.clinica.salud.modules.customerservice.infrastructure.persistence;

import com.clinica.salud.modules.customerservice.domain.model.Complaint;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ComplaintMapper {

    Complaint toDomain(ComplaintEntity entity);

    ComplaintEntity toEntity(Complaint domain);
}
