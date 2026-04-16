package com.clinica.salud.modules.patientportal.domain.port;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto para buscar datos de pacientes desde el módulo de patients.
 * Permite que el portal del paciente verifique la existencia de un paciente por email
 * sin depender directamente de la infraestructura del módulo patients.
 */
public interface PatientLookupPort {

    Optional<PatientInfo> findByEmail(String email);

    record PatientInfo(UUID patientId, String fullName, String email) {}
}
