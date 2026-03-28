package com.clinica.salud.modules.hrm.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class ScheduleResponse {

    private UUID id;
    private UUID employeeId;
    private UUID sedeId;
    private int dayOfWeek;
    private String dayName;
    private LocalTime startTime;
    private LocalTime endTime;
    private int durationMinutes;
    private boolean active;
}
