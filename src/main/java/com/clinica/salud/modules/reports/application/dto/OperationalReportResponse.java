package com.clinica.salud.modules.reports.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class OperationalReportResponse {

    private UUID sedeId;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    // Citas
    private long totalAppointments;
    private long attendedAppointments;
    private long cancelledAppointments;
    private long noShowAppointments;
    private double attendanceRate;      // % atendidas / programadas
    private double noShowRate;          // % no-shows / programadas

    // Tiempos promedio (en minutos)
    private double avgWaitTimeMinutes;
    private double avgConsultationMinutes;
}
