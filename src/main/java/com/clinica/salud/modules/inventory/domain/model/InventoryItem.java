package com.clinica.salud.modules.inventory.domain.model;

import com.clinica.salud.shared.exception.BusinessRuleException;
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

    /**
     * Indica si el stock está por debajo del mínimo requerido.
     */
    public boolean isLowStock() {
        return stock <= minStock;
    }

    /**
     * Indica si hay suficiente stock para dispensar la cantidad pedida.
     */
    public boolean canDispense(int quantity) {
        return stock >= quantity;
    }

    /**
     * Registra una entrada de stock (compra o ajuste positivo).
     */
    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleException("La cantidad a agregar debe ser mayor a cero");
        }
        this.stock += quantity;
    }

    /**
     * Descuenta stock por dispensación. Valida disponibilidad antes de descontar.
     */
    public void dispense(int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleException("La cantidad a dispensar debe ser mayor a cero");
        }
        if (!canDispense(quantity)) {
            throw new BusinessRuleException(
                    "Stock insuficiente. Disponible: " + this.stock + ", requerido: " + quantity);
        }
        this.stock -= quantity;
    }

    /**
     * Ajusta el stock a un valor absoluto (para inventarios físicos/arqueos).
     */
    public void adjustStock(int newStock) {
        if (newStock < 0) {
            throw new BusinessRuleException("El stock no puede ser negativo");
        }
        this.stock = newStock;
    }

    /**
     * Actualiza el nivel mínimo de stock para alertas.
     */
    public void updateMinStock(int newMinStock) {
        if (newMinStock < 0) {
            throw new BusinessRuleException("El stock mínimo no puede ser negativo");
        }
        this.minStock = newMinStock;
    }
}
