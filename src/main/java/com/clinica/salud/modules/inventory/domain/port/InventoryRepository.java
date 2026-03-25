package com.clinica.salud.modules.inventory.domain.port;

import com.clinica.salud.modules.inventory.domain.model.InventoryAlert;
import com.clinica.salud.modules.inventory.domain.model.InventoryItem;
import com.clinica.salud.modules.inventory.domain.model.InventoryMovement;
import com.clinica.salud.modules.inventory.domain.model.InventoryMovementType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository {

    Optional<InventoryItem> findItemByMedicationAndSede(UUID medicationId, UUID sedeId);

    InventoryItem saveItem(InventoryItem item);

    InventoryMovement saveMovement(InventoryMovement movement);

    List<InventoryMovement> findMovementsByItem(UUID itemId);

    List<InventoryAlert> findLowStockAlerts(UUID sedeId);

    int calculateNewStock(int currentStock, InventoryMovementType type, int quantity);
}

