package com.clinica.salud.modules.inventory.application.usecase;

import com.clinica.salud.modules.inventory.application.dto.InventoryItemResponse;
import com.clinica.salud.modules.inventory.application.dto.InventoryMovementResponse;
import com.clinica.salud.modules.inventory.application.dto.RegisterInventoryMovementRequest;
import com.clinica.salud.modules.inventory.domain.model.InventoryItem;
import com.clinica.salud.modules.inventory.domain.model.InventoryMovement;
import com.clinica.salud.modules.inventory.domain.port.InventoryRepository;
import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterInventoryMovementUseCase {

    private final InventoryRepository inventoryRepository;

    public InventoryItemResponse execute(RegisterInventoryMovementRequest request, UUID userId) {
        InventoryItem item = inventoryRepository
                .findItemByMedicationAndSede(request.medicationId(), request.sedeId())
                .orElseGet(() -> InventoryItem.builder()
                        .id(null)
                        .medicationId(request.medicationId())
                        .sedeId(request.sedeId())
                        .stock(0)
                        .minStock(10)
                        .build());

        int newStock = inventoryRepository.calculateNewStock(
                item.getStock(),
                request.type(),
                request.quantity()
        );

        if (newStock < 0) {
            throw new BusinessRuleException("Stock insuficiente para realizar la salida");
        }

        item.setStock(newStock);
        InventoryItem savedItem = inventoryRepository.saveItem(item);

        InventoryMovement movement = InventoryMovement.builder()
                .id(null)
                .itemId(savedItem.getId())
                .type(request.type())
                .quantity(request.quantity())
                .lotNumber(request.lotNumber())
                .expiryDate(request.expiryDate())
                .reason(request.reason())
                .reference(request.reference())
                .createdBy(userId)
                .createdAt(OffsetDateTime.now())
                .build();

        InventoryMovement savedMovement = inventoryRepository.saveMovement(movement);

        // Por ahora no devolvemos el movimiento; solo el estado del ítem.
        return new InventoryItemResponse(
                savedItem.getId(),
                savedItem.getMedicationId(),
                savedItem.getSedeId(),
                savedItem.getStock(),
                savedItem.getMinStock()
        );
    }
}

