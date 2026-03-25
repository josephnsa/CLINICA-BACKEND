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

    /**
     * Inicia la consulta. El paciente debe haber hecho check-in.
     */
    public void startConsultation() {
        if (this.status != AppointmentStatus.CHECKED_IN) {
            throw new BusinessRuleException("La consulta solo puede iniciarse después del check-in");
        }
        this.status = AppointmentStatus.IN_PROGRESS;
    }

    /**
     * Completa la consulta. Solo aplica si está IN_PROGRESS.
     */
    public void complete() {
        if (this.status != AppointmentStatus.IN_PROGRESS) {
            throw new BusinessRuleException("Solo se puede completar una cita en estado IN_PROGRESS");
        }
        this.status = AppointmentStatus.ATTENDED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marca la cita como no-show cuando el paciente no se presentó.
     * Solo aplica si está PENDING o CONFIRMED.
     */
    public void markNoShow() {
        if (this.status != AppointmentStatus.PENDING && this.status != AppointmentStatus.CONFIRMED) {
            throw new BusinessRuleException("Solo se puede marcar como no-show una cita PENDING o CONFIRMED");
        }
        this.status = AppointmentStatus.NO_SHOW;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reprograma la cita a un nuevo horario.
     * No se puede reprogramar si ya fue atendida, cancelada o marcada como no-show.
     */
    public void reschedule(LocalDateTime newStart, LocalDateTime newEnd) {
        if (this.status == AppointmentStatus.ATTENDED
                || this.status == AppointmentStatus.CANCELLED
                || this.status == AppointmentStatus.NO_SHOW) {
            throw new BusinessRuleException("No se puede reprogramar una cita en estado " + this.status);
        }
        if (newStart == null || newEnd == null || !newEnd.isAfter(newStart)) {
            throw new BusinessRuleException("El nuevo horario es inválido");
        }
        if (newStart.isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("No se puede reprogramar a una fecha en el pasado");
        }
        this.startTime = newStart;
        this.endTime = newEnd;
        this.status = AppointmentStatus.PENDING;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Indica si la cita está activa (no terminada ni cancelada).
     */
    public boolean isActive() {
        return this.status != AppointmentStatus.ATTENDED
                && this.status != AppointmentStatus.CANCELLED
                && this.status != AppointmentStatus.NO_SHOW;
    }

    /**
     * Indica si la cita ya pasó su horario de inicio sin haberse atendido.
     */
    public boolean isOverdue() {
        return isActive() && LocalDateTime.now().isAfter(this.startTime);
    }
}
