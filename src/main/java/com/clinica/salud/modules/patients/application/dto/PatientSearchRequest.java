package com.clinica.salud.modules.patients.application.dto;

/**
 * Criterios de búsqueda de pacientes (nombre o documento, filtro activos).
 */
public record PatientSearchRequest(
        String query,
        String docNumber,
        boolean activeOnly
) {}
