package com.clinica.salud.modules.patientportal.domain.port;

import com.clinica.salud.modules.patientportal.application.dto.PortalPrescriptionResponse;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de lectura de prescripciones para el portal del paciente.
 */
public interface PortalPrescriptionPort {

    List<PortalPrescriptionResponse> findByPatientId(UUID patientId);
}
