package com.clinica.salud.modules.hrm.domain.model;

import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private UUID id;
    private UUID userId;
    private UUID sedeId;
    private UUID specialtyId;
    private String firstName;
    private String lastName;
    private String docType;
    private String docNumber;
    private String licenseNumber;   // número de colegiatura
    private String position;        // cargo (Doctor, Enfermero, Técnico, etc.)
    private LocalDate hireDate;
    private boolean isActive;
    private String phone;
    private String email;
    private LocalDateTime createdAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Años de antigüedad en la clínica desde la fecha de contratación.
     */
    public int getSeniority() {
        if (hireDate == null) return 0;
        return Period.between(hireDate, LocalDate.now()).getYears();
    }

    /**
     * Indica si el empleado tiene número de colegiatura registrado (obligatorio para médicos).
     */
    public boolean isLicensed() {
        return licenseNumber != null && !licenseNumber.isBlank();
    }

    /**
     * Desactiva al empleado (cese o baja).
     */
    public void deactivate() {
        if (!this.isActive) {
            throw new BusinessRuleException("El empleado ya está inactivo");
        }
        this.isActive = false;
    }

    /**
     * Reactiva al empleado (reingreso).
     */
    public void reactivate() {
        if (this.isActive) {
            throw new BusinessRuleException("El empleado ya está activo");
        }
        this.isActive = true;
    }

    /**
     * Valida datos mínimos requeridos para registrar un empleado.
     */
    public void validate() {
        if (firstName == null || firstName.isBlank()) {
            throw new BusinessRuleException("El nombre del empleado es obligatorio");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new BusinessRuleException("El apellido del empleado es obligatorio");
        }
        if (position == null || position.isBlank()) {
            throw new BusinessRuleException("El cargo del empleado es obligatorio");
        }
    }
}
