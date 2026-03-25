package com.clinica.salud.modules.customerservice.domain.model;

import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Complaint {

    private UUID id;
    private UUID patientId;
    private UUID sedeId;
    private ComplaintType type;
    private String subject;
    private String description;
    private ComplaintStatus status;
    private ComplaintPriority priority;
    private UUID assignedTo;
    private LocalDateTime resolvedAt;
    private String resolution;
    private UUID createdBy;
    private LocalDateTime createdAt;

    /**
     * Asigna el reclamo a un responsable para su gestión.
     */
    public void assignTo(UUID userId) {
        if (userId == null) {
            throw new BusinessRuleException("Debe especificarse un responsable para asignar el reclamo");
        }
        if (this.status == ComplaintStatus.CLOSED || this.status == ComplaintStatus.RESOLVED) {
            throw new BusinessRuleException("No se puede reasignar un reclamo ya " + this.status);
        }
        this.assignedTo = userId;
        this.status = ComplaintStatus.IN_PROGRESS;
    }

    /**
     * Resuelve el reclamo con una descripción de la resolución tomada.
     */
    public void resolve(String resolution) {
        if (resolution == null || resolution.isBlank()) {
            throw new BusinessRuleException("Debe describir la resolución del reclamo");
        }
        if (this.status == ComplaintStatus.CLOSED) {
            throw new BusinessRuleException("El reclamo ya está cerrado");
        }
        this.resolution = resolution;
        this.status = ComplaintStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
    }

    /**
     * Cierra el reclamo definitivamente (confirmación del paciente o plazo cumplido).
     */
    public void close() {
        if (this.status != ComplaintStatus.RESOLVED) {
            throw new BusinessRuleException("Solo se puede cerrar un reclamo en estado RESOLVED");
        }
        this.status = ComplaintStatus.CLOSED;
    }

    /**
     * Escala la prioridad del reclamo.
     */
    public void escalatePriority() {
        this.priority = switch (this.priority) {
            case LOW -> ComplaintPriority.MEDIUM;
            case MEDIUM -> ComplaintPriority.HIGH;
            case HIGH -> ComplaintPriority.CRITICAL;
            case CRITICAL -> throw new BusinessRuleException("El reclamo ya está en prioridad máxima (CRITICAL)");
        };
    }

    /**
     * Indica si el reclamo está pendiente de atención.
     */
    public boolean isPending() {
        return this.status == ComplaintStatus.OPEN;
    }

    /**
     * Indica si el reclamo requiere atención urgente.
     */
    public boolean isUrgent() {
        return this.priority == ComplaintPriority.HIGH || this.priority == ComplaintPriority.CRITICAL;
    }

    public void validate() {
        if (subject == null || subject.isBlank()) {
            throw new BusinessRuleException("El asunto del reclamo es obligatorio");
        }
        if (description == null || description.isBlank()) {
            throw new BusinessRuleException("La descripción del reclamo es obligatoria");
        }
        if (type == null) {
            throw new BusinessRuleException("El tipo de reclamo es obligatorio");
        }
    }
}
