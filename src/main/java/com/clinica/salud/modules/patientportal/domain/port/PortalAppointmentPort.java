package com.clinica.salud.modules.patientportal.domain.port;

import com.clinica.salud.modules.patientportal.application.dto.PortalAppointmentResponse;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de lectura de citas para el portal del paciente.
 * Implementado por un adapter con JdbcTemplate en la capa de infraestructura.
 */
public interface PortalAppointmentPort {

    List<PortalAppointmentResponse> findByPatientId(UUID patientId);
}
