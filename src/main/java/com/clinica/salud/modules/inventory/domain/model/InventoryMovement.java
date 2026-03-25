package com.clinica.salud.modules.inventory.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryMovement {

    private UUID id;
    private UUID itemId;
    private InventoryMovementType type;
    private int quantity;
    private String lotNumber;
    private LocalDate expiryDate;
    private String reason;
    private String reference;
    private UUID createdBy;
    private OffsetDateTime createdAt;
}

