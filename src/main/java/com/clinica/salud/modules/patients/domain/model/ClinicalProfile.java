package com.clinica.salud.modules.patients.domain.model;

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
public class ClinicalProfile {

    private UUID id;
    private UUID patientId;
    private String allergies;
    private String personalHistory;
    private String familyHistory;
    private String surgicalHistory;
    private String currentMeds;
    private LocalDateTime updatedAt;

    /**
     * Verifica si el paciente tiene alergia a un medicamento/sustancia dada.
     * Búsqueda insensible a mayúsculas.
     */
    public boolean hasAllergyTo(String substance) {
        if (allergies == null || substance == null) return false;
        return allergies.toLowerCase().contains(substance.toLowerCase());
    }

    /**
     * Agrega una alergia a la lista existente. Evita duplicados.
     */
    public void addAllergy(String newAllergy) {
        if (newAllergy == null || newAllergy.isBlank()) return;
        if (hasAllergyTo(newAllergy)) return;
        this.allergies = (this.allergies == null || this.allergies.isBlank())
                ? newAllergy
                : this.allergies + ", " + newAllergy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Agrega un medicamento actual al perfil.
     */
    public void addCurrentMedication(String medication) {
        if (medication == null || medication.isBlank()) return;
        this.currentMeds = (this.currentMeds == null || this.currentMeds.isBlank())
                ? medication
                : this.currentMeds + ", " + medication;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Indica si el perfil clínico tiene información de riesgo relevante.
     */
    public boolean hasRiskFactors() {
        return (allergies != null && !allergies.isBlank())
                || (personalHistory != null && !personalHistory.isBlank())
                || (familyHistory != null && !familyHistory.isBlank());
    }
}
