package com.clinica.salud.modules.prescription.domain.model;

import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Prescription {

    private UUID id;
    private UUID patientId;
    private UUID doctorId;
    private UUID appointmentId;
    private UUID diagnosisId;
    private String notes;
    private PrescriptionStatus status;
    private OffsetDateTime createdAt;
    private UUID createdBy;
    private List<PrescriptionItem> items;

    /**
     * Dispensa la receta. Solo puede dispensarse si está ACTIVE.
     */
    public void dispense() {
        if (this.status != PrescriptionStatus.ACTIVE) {
            throw new BusinessRuleException("Solo se puede dispensar una receta en estado ACTIVE");
        }
        if (items == null || items.isEmpty()) {
            throw new BusinessRuleException("La receta no tiene ítems de medicamento");
        }
        this.status = PrescriptionStatus.COMPLETED;
    }

    /**
     * Cancela la receta. No se puede cancelar si ya fue completada.
     */
    public void cancel() {
        if (this.status == PrescriptionStatus.COMPLETED) {
            throw new BusinessRuleException("No se puede cancelar una receta ya completada (dispensada)");
        }
        this.status = PrescriptionStatus.CANCELLED;
    }

    /**
     * Indica si la receta sigue activa para dispensación.
     */
    public boolean isDispensable() {
        return this.status == PrescriptionStatus.ACTIVE;
    }

    /**
     * Valida que la receta tenga los datos mínimos requeridos antes de persistir.
     */
    public void validate() {
        if (patientId == null) {
            throw new BusinessRuleException("La receta debe estar asociada a un paciente");
        }
        if (doctorId == null) {
            throw new BusinessRuleException("La receta debe estar firmada por un médico");
        }
        if (items == null || items.isEmpty()) {
            throw new BusinessRuleException("La receta debe tener al menos un medicamento");
        }
        for (PrescriptionItem item : items) {
            item.validate();
        }
    }

    /**
     * Número total de ítems en la receta.
     */
    public int getTotalItems() {
        return items == null ? 0 : items.size();
    }
}
