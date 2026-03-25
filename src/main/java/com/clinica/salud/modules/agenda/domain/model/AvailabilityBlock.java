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
public class AvailabilityBlock {

    private UUID id;
    private UUID doctorId;
    private UUID sedeId;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private String reason;

    public void validate() {
        if (startDt == null || endDt == null) {
            throw new BusinessRuleException("Fecha de inicio y fin son obligatorias");
        }
        if (!endDt.isAfter(startDt)) {
            throw new BusinessRuleException("La fecha de fin debe ser posterior a la de inicio");
        }
    }

    public boolean overlaps(LocalDateTime start, LocalDateTime end) {
        return startDt.isBefore(end) && endDt.isAfter(start);
    }
}
