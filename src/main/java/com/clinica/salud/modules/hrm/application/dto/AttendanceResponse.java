package com.clinica.salud.modules.hrm.application.dto;

import com.clinica.salud.modules.hrm.domain.model.AttendanceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AttendanceResponse {

    private UUID id;
    private UUID employeeId;
    private UUID sedeId;
    private LocalDate date;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer minutesWorked;
    private AttendanceStatus status;
    private String notes;
}
