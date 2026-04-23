package com.clinica.salud.modules.patients.infrastructure.persistence;

import com.clinica.salud.modules.patients.domain.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    /**
     * Campo boolean {@code isActive} en entidad: MapStruct lo expone como {@code active}
     * (par isActive/setActive). Sin este mapping, {@code isActive} queda en false al
     * reconstruir el dominio y la API lista pacientes como inactivos incorrectamente.
     */
    @Mapping(target = "isActive", source = "active")
    Patient toDomain(PatientEntity entity);

    PatientEntity toEntity(Patient domain);
}
