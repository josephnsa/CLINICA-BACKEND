package com.clinica.salud.modules.hrm.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ProductivityReport {

    private UUID employeeId;
    private String employeeFullName;
    private String position;
    private LocalDate from;
    private LocalDate to;

    // Asistencia
    private int totalDays;
    private int presentDays;
    private int absentDays;
    private int lateDays;
    private int excusedDays;
    private double attendanceRate;     // % días presentes / días laborables

    // Tiempo trabajado
    private int totalMinutesWorked;
    private double averageMinutesPerDay;

    // Productividad clínica (solo aplica para médicos con user_id enlazado)
    private int appointmentsAttended;
    private double averageAppointmentDurationMinutes;
}
