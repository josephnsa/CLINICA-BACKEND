package com.clinica.salud.modules.patients.domain.model;

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
public class Patient {

    private UUID id;
    private String docType;
    private String docNumber;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String bloodType;
    private String emergencyName;
    private String emergencyPhone;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getFullName() {
        if (firstName == null && lastName == null) return "";
        if (firstName == null) return lastName != null ? lastName : "";
        if (lastName == null) return firstName;
        return firstName + " " + lastName;
    }

    /**
     * Edad en años a partir de birthDate. Retorna 0 si birthDate es null.
     */
    public int getAge() {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Indica si el paciente es menor de edad (menos de 18 años).
     */
    public boolean isMinor() {
        return getAge() < 18;
    }

    /**
     * Desactiva al paciente. No se puede desactivar si ya está inactivo.
     */
    public void deactivate() {
        if (!this.isActive) {
            throw new com.clinica.salud.shared.exception.BusinessRuleException("El paciente ya está inactivo");
        }
        this.isActive = false;
        this.updatedAt = java.time.LocalDateTime.now();
    }

    /**
     * Reactiva al paciente.
     */
    public void reactivate() {
        if (this.isActive) {
            throw new com.clinica.salud.shared.exception.BusinessRuleException("El paciente ya está activo");
        }
        this.isActive = true;
        this.updatedAt = java.time.LocalDateTime.now();
    }

    /**
     * Actualiza el contacto de emergencia validando que el teléfono no esté vacío.
     */
    public void updateEmergencyContact(String name, String phone) {
        if (phone == null || phone.isBlank()) {
            throw new com.clinica.salud.shared.exception.BusinessRuleException("El teléfono del contacto de emergencia es obligatorio");
        }
        this.emergencyName = name;
        this.emergencyPhone = phone;
        this.updatedAt = java.time.LocalDateTime.now();
    }

    /**
     * Valida que el paciente tenga los datos mínimos requeridos.
     */
    public void validate() {
        if (docNumber == null || docNumber.isBlank()) {
            throw new com.clinica.salud.shared.exception.BusinessRuleException("El número de documento es obligatorio");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new com.clinica.salud.shared.exception.BusinessRuleException("El nombre del paciente es obligatorio");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new com.clinica.salud.shared.exception.BusinessRuleException("El apellido del paciente es obligatorio");
        }
    }
}
