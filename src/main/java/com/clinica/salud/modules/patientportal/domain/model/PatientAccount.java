package com.clinica.salud.modules.patientportal.domain.model;

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
public class PatientAccount {

    private UUID id;
    private UUID patientId;
    private String email;
    private String password;
    private boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    /**
     * Registra el acceso del paciente al portal.
     */
    public void recordLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    /**
     * Desactiva la cuenta del portal del paciente.
     */
    public void deactivate() {
        if (!this.isActive) {
            throw new BusinessRuleException("La cuenta ya está desactivada");
        }
        this.isActive = false;
    }

    public void validate() {
        if (email == null || email.isBlank()) {
            throw new BusinessRuleException("El email es obligatorio");
        }
        if (password == null || password.isBlank()) {
            throw new BusinessRuleException("La contraseña es obligatoria");
        }
        if (patientId == null) {
            throw new BusinessRuleException("El paciente es obligatorio");
        }
    }
}
