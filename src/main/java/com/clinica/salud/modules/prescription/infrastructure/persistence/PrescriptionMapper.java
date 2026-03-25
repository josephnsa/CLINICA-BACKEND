package com.clinica.salud.modules.prescription.infrastructure.persistence;

import com.clinica.salud.modules.prescription.domain.model.Prescription;
import com.clinica.salud.modules.prescription.domain.model.PrescriptionItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PrescriptionMapper {

    @Mapping(target = "items", ignore = true)
    PrescriptionEntity toEntity(Prescription domain);

    @Mapping(target = "items", ignore = true)
    Prescription toDomain(PrescriptionEntity entity);

    PrescriptionItemEntity toEntity(PrescriptionItem item);

    PrescriptionItem toDomain(PrescriptionItemEntity entity);

    @AfterMapping
    default void linkItems(@MappingTarget PrescriptionEntity entity, Prescription domain) {
        entity.getItems().clear();
        if (domain.getItems() != null) {
            for (PrescriptionItem item : domain.getItems()) {
                PrescriptionItemEntity itemEntity = toEntity(item);
                itemEntity.setPrescription(entity);
                entity.getItems().add(itemEntity);
            }
        }
    }

    @AfterMapping
    default void linkDomainItems(@MappingTarget Prescription prescription, PrescriptionEntity entity) {
        prescription.setItems(entity.getItems().stream()
                .map(this::toDomain)
                .toList());
    }
}

