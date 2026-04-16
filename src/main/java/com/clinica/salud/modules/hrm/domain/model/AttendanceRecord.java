package com.clinica.salud.modules.hrm.domain.model;

import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRecord {

    private UUID id;
    private UUID employeeId;
    private UUID sedeId;
    private LocalDate date;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer minutesWorked;
    private AttendanceStatus status;
    private String notes;
    private LocalDateTime createdAt;

    /**
     * Registra la salida y calcula los minutos trabajados.
     */
    public void registerCheckOut(LocalDateTime checkOutTime) {
        if (this.checkIn == null) {
            throw new BusinessRuleException("No se puede registrar salida sin haber registrado entrada");
        }
        if (this.checkOut != null) {
            throw new BusinessRuleException("La salida ya fue registrada para este día");
        }
        if (checkOutTime.isBefore(this.checkIn)) {
            throw new BusinessRuleException("La hora de salida no puede ser anterior a la hora de entrada");
        }
        this.checkOut = checkOutTime;
        this.minutesWorked = (int) ChronoUnit.MINUTES.between(this.checkIn, checkOutTime);
    }

    /**
     * Evalúa si la entrada fue tardía respecto a la hora de inicio de turno esperada.
     * Se considera tardanza si el empleado llega más de 10 minutos después.
     *
     * @param expectedStart hora de inicio del turno según horario
     */
    public void evaluateLate(java.time.LocalTime expectedStart) {
        if (this.checkIn == null || expectedStart == null) return;
        java.time.LocalTime actualTime = this.checkIn.toLocalTime();
        if (actualTime.isAfter(expectedStart.plusMinutes(10))) {
            this.status = AttendanceStatus.LATE;
        }
    }

    /**
     * Indica si el empleado trabajó al menos la jornada mínima (4 horas).
     */
    public boolean completedMinimumShift() {
        return minutesWorked != null && minutesWorked >= 240;
    }

    public void validate() {
        if (employeeId == null) throw new BusinessRuleException("El empleado es obligatorio");
        if (date == null) throw new BusinessRuleException("La fecha es obligatoria");
    }
}
