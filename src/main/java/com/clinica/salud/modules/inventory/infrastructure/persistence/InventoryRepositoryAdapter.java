package com.clinica.salud.modules.inventory.infrastructure.persistence;

import com.clinica.salud.modules.inventory.domain.model.InventoryAlert;
import com.clinica.salud.modules.inventory.domain.model.InventoryItem;
import com.clinica.salud.modules.inventory.domain.model.InventoryMovement;
import com.clinica.salud.modules.inventory.domain.model.InventoryMovementType;
import com.clinica.salud.modules.inventory.domain.port.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InventoryRepositoryAdapter implements InventoryRepository {

    private final InventoryItemJpaRepository itemJpaRepository;
    private final InventoryMovementJpaRepository movementJpaRepository;
    private final InventoryItemMapper itemMapper;
    private final InventoryMovementMapper movementMapper;

    @Override
    public Optional<InventoryItem> findItemByMedicationAndSede(UUID medicationId, UUID sedeId) {
        return itemJpaRepository.findByMedicationIdAndSedeId(medicationId, sedeId)
                .map(itemMapper::toDomain);
    }

    @Override
    public InventoryItem saveItem(InventoryItem item) {
        InventoryItemEntity entity = itemMapper.toEntity(item);
        InventoryItemEntity saved = itemJpaRepository.save(entity);
        return itemMapper.toDomain(saved);
    }

    @Override
    public InventoryMovement saveMovement(InventoryMovement movement) {
        InventoryMovementEntity entity = movementMapper.toEntity(movement);
        InventoryItemEntity item = itemJpaRepository.getReferenceById(movement.getItemId());
        entity.setItem(item);
        InventoryMovementEntity saved = movementJpaRepository.save(entity);
        return movementMapper.toDomain(saved);
    }

    @Override
    public List<InventoryMovement> findMovementsByItem(UUID itemId) {
        return movementJpaRepository.findByItemIdOrderByCreatedAtDesc(itemId).stream()
                .map(movementMapper::toDomain)
                .toList();
    }

    @Override
    public List<InventoryAlert> findLowStockAlerts(UUID sedeId) {
        LocalDate today = LocalDate.now();
        return itemJpaRepository.findLowStock(sedeId).stream()
                .map(entity -> InventoryAlert.builder()
                        .itemId(entity.getId())
                        .medicationId(entity.getMedicationId())
                        .sedeId(entity.getSedeId())
                        .stock(entity.getStock())
                        .minStock(entity.getMinStock())
                        .nearestExpiryDate(itemJpaRepository.findNearestExpiryDate(entity.getId(), today))
                        .build())
                .toList();
    }

    @Override
    public int calculateNewStock(int currentStock, InventoryMovementType type, int quantity) {
        return switch (type) {
            case IN -> currentStock + quantity;
            case OUT -> currentStock - quantity;
            case ADJUST -> quantity;
        };
    }
}

