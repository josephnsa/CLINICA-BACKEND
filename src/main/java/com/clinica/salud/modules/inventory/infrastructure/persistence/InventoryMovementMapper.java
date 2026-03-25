package com.clinica.salud.modules.inventory.infrastructure.persistence;

import com.clinica.salud.modules.inventory.domain.model.InventoryMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMovementMapper {

    @Mapping(target = "itemId", source = "item.id")
    InventoryMovement toDomain(InventoryMovementEntity entity);

    @Mapping(target = "item", ignore = true)
    InventoryMovementEntity toEntity(InventoryMovement domain);
}

