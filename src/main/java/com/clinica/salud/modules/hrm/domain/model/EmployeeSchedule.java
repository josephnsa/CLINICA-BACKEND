package com.clinica.salud.modules.hrm.domain.model;

import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSchedule {

    private UUID id;
    private UUID employeeId;
    private UUID sedeId;
    private int dayOfWeek;          // 0=Lunes, 6=Domingo
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isActive;

    private static final String[] DAY_NAMES = {
        "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
    };

    public String getDayName() {
        return DAY_NAMES[dayOfWeek];
    }

    /**
     * Duración del turno en minutos.
     */
    public int getDurationMinutes() {
        return (int) java.time.Duration.between(startTime, endTime).toMinutes();
    }

    /**
     * Verifica si el horario cubre un tiempo dado (para validar citas).
     */
    public boolean covers(LocalTime time) {
        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }

    /**
     * Valida que el horario sea coherente.
     */
    public void validate() {
        if (dayOfWeek < 0 || dayOfWeek > 6) {
            throw new BusinessRuleException("El día de la semana debe estar entre 0 (Lunes) y 6 (Domingo)");
        }
        if (startTime == null || endTime == null) {
            throw new BusinessRuleException("La hora de inicio y fin son obligatorias");
        }
        if (!endTime.isAfter(startTime)) {
            throw new BusinessRuleException("La hora de fin debe ser posterior a la hora de inicio");
        }
        if (getDurationMinutes() < 30) {
            throw new BusinessRuleException("El turno debe tener al menos 30 minutos de duración");
        }
    }
}
