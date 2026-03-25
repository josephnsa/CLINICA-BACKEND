package com.clinica.salud.modules.inventory.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryAlert {

    private UUID itemId;
    private UUID medicationId;
    private UUID sedeId;
    private int stock;
    private int minStock;
    private LocalDate nearestExpiryDate;
}

