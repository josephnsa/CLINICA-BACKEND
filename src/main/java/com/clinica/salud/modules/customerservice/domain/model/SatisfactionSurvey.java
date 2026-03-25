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
public class SatisfactionSurvey {

    private UUID id;
    private UUID patientId;
    private UUID appointmentId;
    private int score;              // 1 a 5 (NPS simplificado)
    private String comment;
    private LocalDateTime createdAt;

    /**
     * Califica la experiencia. El score debe estar entre 1 y 5.
     */
    public void rate(int score, String comment) {
        if (score < 1 || score > 5) {
            throw new BusinessRuleException("La calificación debe estar entre 1 y 5");
        }
        this.score = score;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Indica si la experiencia fue positiva (score >= 4).
     */
    public boolean isPositive() {
        return score >= 4;
    }

    /**
     * Indica si la experiencia fue negativa (score <= 2).
     */
    public boolean isNegative() {
        return score <= 2;
    }

    /**
     * Etiqueta descriptiva del score.
     */
    public String getScoreLabel() {
        return switch (score) {
            case 1 -> "Muy insatisfecho";
            case 2 -> "Insatisfecho";
            case 3 -> "Neutral";
            case 4 -> "Satisfecho";
            case 5 -> "Muy satisfecho";
            default -> "Sin calificación";
        };
    }
}
