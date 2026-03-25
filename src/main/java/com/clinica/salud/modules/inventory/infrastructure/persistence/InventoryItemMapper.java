package com.clinica.salud.modules.inventory.infrastructure.persistence;

import com.clinica.salud.modules.inventory.domain.model.InventoryItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryItemMapper {

    InventoryItem toDomain(InventoryItemEntity entity);

    InventoryItemEntity toEntity(InventoryItem domain);
}

