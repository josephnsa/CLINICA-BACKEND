package com.clinica.salud.modules.catalog.service.infrastructure.persistence;

import com.clinica.salud.modules.catalog.service.domain.model.MedicalService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(target = "specialtyId", source = "specialty.id")
    /** MapStruct no enlaza solo el getter {@code isActive()} → hay que forzarlo o siempre queda {@code false} en dominio. */
    @Mapping(target = "isActive", expression = "java(entity.isActive())")
    MedicalService toDomain(ServiceEntity entity);

    /**
     * {@code specialty} se resuelve en el adaptador con una referencia JPA gestionada;
     * no mapear aquí para evitar TransientObjectException al hacer flush.
     */
    @Mapping(target = "specialty", ignore = true)
    ServiceEntity toEntity(MedicalService domain);
}

