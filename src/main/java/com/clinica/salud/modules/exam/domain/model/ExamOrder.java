package com.clinica.salud.modules.exam.domain.model;

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
public class ExamOrder {

    private UUID id;
    private UUID patientId;
    private UUID doctorId;
    private UUID appointmentId;
    private ExamOrderStatus status;
    private String notes;
    private OffsetDateTime createdAt;
    private UUID createdBy;
    private UUID signedBy;
    private OffsetDateTime signedAt;
    private List<ExamOrderItem> items;

    /**
     * Pasa la orden a EN_PROCESO. Solo desde PENDIENTE.
     */
    public void startProcessing() {
        if (this.status != ExamOrderStatus.PENDIENTE) {
            throw new BusinessRuleException("La orden debe estar en estado PENDIENTE para iniciar procesamiento");
        }
        this.status = ExamOrderStatus.EN_PROCESO;
    }

    /**
     * Marca la orden como LISTO cuando todos los resultados están registrados.
     */
    public void markReady() {
        if (this.status != ExamOrderStatus.EN_PROCESO) {
            throw new BusinessRuleException("La orden debe estar EN_PROCESO para marcarse como lista");
        }
        boolean allItemsHaveResults = items != null && items.stream()
                .allMatch(item -> item.getResultText() != null && !item.getResultText().isBlank());
        if (!allItemsHaveResults) {
            throw new BusinessRuleException("Todos los exámenes deben tener resultados registrados antes de marcar como lista");
        }
        this.status = ExamOrderStatus.LISTO;
    }

    /**
     * Registra la entrega de resultados al paciente.
     */
    public void deliver() {
        if (this.status != ExamOrderStatus.LISTO) {
            throw new BusinessRuleException("La orden debe estar LISTO para poder entregarse");
        }
        this.status = ExamOrderStatus.ENTREGADO;
    }

    /**
     * Firma profesional de la orden de examen (validación médica).
     */
    public void sign(UUID professionalId) {
        if (this.status == ExamOrderStatus.PENDIENTE) {
            throw new BusinessRuleException("No se puede firmar una orden que no ha sido procesada");
        }
        if (professionalId == null) {
            throw new BusinessRuleException("Se requiere identificar al profesional firmante");
        }
        this.signedBy = professionalId;
        this.signedAt = OffsetDateTime.now();
    }

    /**
     * Indica si la orden ya fue firmada por un profesional.
     */
    public boolean isSigned() {
        return this.signedBy != null && this.signedAt != null;
    }

    /**
     * Valida que la orden tenga los datos mínimos antes de persistir.
     */
    public void validate() {
        if (patientId == null) {
            throw new BusinessRuleException("La orden debe estar asociada a un paciente");
        }
        if (doctorId == null) {
            throw new BusinessRuleException("La orden debe estar emitida por un médico");
        }
        if (items == null || items.isEmpty()) {
            throw new BusinessRuleException("La orden debe incluir al menos un examen");
        }
    }
}
