package com.clinica.salud.modules.patients.domain.model;

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
public class PatientConsent {

    private UUID id;
    private UUID patientId;
    private String type;
    private String fileUrl;
    private LocalDateTime signedAt;
    private UUID createdBy;

    public void validate() {
        if (patientId == null) {
            throw new BusinessRuleException("El consentimiento debe estar asociado a un paciente");
        }
        if (type == null || type.isBlank()) {
            throw new BusinessRuleException("El tipo de consentimiento es obligatorio");
        }
    }

    public boolean isSigned() {
        return signedAt != null;
    }
}
