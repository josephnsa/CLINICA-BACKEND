package com.clinica.salud.modules.inventory.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItem {

    private UUID id;
    private UUID medicationId;
    private UUID sedeId;
    private int stock;
    private int minStock;
}

