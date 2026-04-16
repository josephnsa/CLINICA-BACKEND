package com.clinica.salud.modules.patientportal.domain.port;

import com.clinica.salud.modules.patientportal.application.dto.PortalExamResponse;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de lectura de resultados de exámenes para el portal del paciente.
 */
public interface PortalExamPort {

    List<PortalExamResponse> findByPatientId(UUID patientId);
}
