package com.clinica.salud.modules.inventory.application.usecase;

import com.clinica.salud.modules.inventory.application.dto.InventoryAlertResponse;
import com.clinica.salud.modules.inventory.domain.port.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetLowStockAlertsUseCase {

    private final InventoryRepository inventoryRepository;

    public List<InventoryAlertResponse> execute(UUID sedeId) {
        return inventoryRepository.findLowStockAlerts(sedeId).stream()
                .map(alert -> new InventoryAlertResponse(
                        alert.getItemId(),
                        alert.getMedicationId(),
                        alert.getSedeId(),
                        alert.getStock(),
                        alert.getMinStock(),
                        alert.getNearestExpiryDate()
                ))
                .toList();
    }
}

