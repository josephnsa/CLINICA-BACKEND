package com.clinica.salud.modules.agenda.domain.model;

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
public class Appointment {
    private UUID id;
    private UUID patientId;
    private UUID doctorId;
    private UUID serviceId;
    private UUID sedeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status;
    private String notes;
    private String cancellationReason;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Cancela la cita. No permite cancelar si ya fue atendida.
     */
    public void cancel(String reason) {
        if (this.status == AppointmentStatus.ATTENDED) {
            throw new BusinessRuleException("No se puede cancelar una cita ya atendida");
        }
        this.status = AppointmentStatus.CANCELLED;
        this.cancellationReason = reason;
    }

    /**
     * Confirma la cita. Solo aplica si está PENDING.
     */
    public void confirm() {
        if (this.status != AppointmentStatus.PENDING) {
            throw new BusinessRuleException("Solo se puede confirmar una cita en estado PENDING");
        }
        this.status = AppointmentStatus.CONFIRMED;
    }

    /**
     * Registra check-in. Solo aplica si está CONFIRMED.
     */
    public void checkIn() {
        if (this.status != AppointmentStatus.CONFIRMED) {
            throw new BusinessRuleException("Solo se puede hacer check-in en una cita CONFIRMED");
        }
        this.status = AppointmentStatus.CHECKED_IN;
    }
}
