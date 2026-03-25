package com.clinica.salud.modules.agenda.domain.model;

import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Triage {

    private UUID id;
    private UUID appointmentId;
    private UUID patientId;
    private UUID recordedBy;

    // Signos vitales
    private String bloodPressure;
    private Integer heartRate;
    private Integer respiratoryRate;
    private BigDecimal temperature;
    private Integer oxygenSaturation;
    private BigDecimal weight;
    private BigDecimal height;

    private TriageLevel triageLevel;
    private String notes;
    private LocalDateTime recordedAt;

    public void validate() {
        if (appointmentId == null) {
            throw new BusinessRuleException("El triaje debe estar asociado a una cita");
        }
        if (patientId == null) {
            throw new BusinessRuleException("El triaje debe estar asociado a un paciente");
        }
        if (triageLevel == null) {
            triageLevel = TriageLevel.NORMAL;
        }
    }

    public boolean isCritical() {
        return triageLevel == TriageLevel.EMERGENCY || triageLevel == TriageLevel.URGENT;
    }

    public Double getBmi() {
        if (weight == null || height == null || height.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        double heightM = height.doubleValue() / 100.0;
        return weight.doubleValue() / (heightM * heightM);
    }

    public boolean hasLowOxygen() {
        return oxygenSaturation != null && oxygenSaturation < 95;
    }

    public boolean hasFever() {
        return temperature != null && temperature.doubleValue() >= 38.0;
    }
}
