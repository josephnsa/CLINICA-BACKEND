package com.clinica.salud.modules.agenda.domain.model;

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
public class DoctorAvailabilityRule {

    private UUID id;
    private UUID doctorId;
    private UUID sedeId;
    private int dayOfWeek;      // 1=Lunes ... 7=Domingo
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isActive;

    public void validate() {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new BusinessRuleException("El día debe estar entre 1 (Lunes) y 7 (Domingo)");
        }
        if (startTime == null || endTime == null) {
            throw new BusinessRuleException("Hora de inicio y fin son obligatorias");
        }
        if (!endTime.isAfter(startTime)) {
            throw new BusinessRuleException("La hora de fin debe ser posterior a la de inicio");
        }
    }

    public boolean coversTime(LocalTime time) {
        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }
}
