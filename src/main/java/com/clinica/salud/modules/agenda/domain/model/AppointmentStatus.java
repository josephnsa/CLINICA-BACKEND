package com.clinica.salud.modules.agenda.domain.model;

/**
 * Coincide con el CHECK de la tabla appointments en BD.
 */
public enum AppointmentStatus {
    PENDING,
    CONFIRMED,
    CHECKED_IN,
    IN_PROGRESS,
    ATTENDED,
    CANCELLED,
    NO_SHOW
}
