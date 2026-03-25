package com.clinica.salud.modules.prescription.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionItem {

    private UUID id;
    private UUID medicationId;
    private String dose;
    private String frequency;
    private String duration;
    private String route;
    private String instructions;
    private int quantity;
    private boolean dispensed;

    /**
     * Valida que el ítem tenga los datos mínimos para una receta válida.
     */
    public void validate() {
        if (medicationId == null) {
            throw new com.clinica.salud.shared.exception.BusinessRuleException("Cada ítem debe tener un medicamento asociado");
        }
        if (dose == null || dose.isBlank()) {
            throw new com.clinica.salud.shared.exception.BusinessRuleException("La dosis es obligatoria en cada medicamento recetado");
        }
        if (quantity <= 0) {
            throw new com.clinica.salud.shared.exception.BusinessRuleException("La cantidad del medicamento debe ser mayor a cero");
        }
    }
}

