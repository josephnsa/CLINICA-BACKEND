package com.clinica.salud.modules.hrm.infrastructure.web;

import com.clinica.salud.modules.hrm.application.dto.CreateEmployeeRequest;
import com.clinica.salud.modules.hrm.application.dto.CreateScheduleRequest;
import com.clinica.salud.modules.hrm.application.dto.EmployeeResponse;
import com.clinica.salud.modules.hrm.application.dto.ScheduleResponse;
import com.clinica.salud.modules.hrm.application.usecase.CreateEmployeeUseCase;
import com.clinica.salud.modules.hrm.application.usecase.CreateScheduleUseCase;
import com.clinica.salud.modules.hrm.application.usecase.DeactivateEmployeeUseCase;
import com.clinica.salud.modules.hrm.application.usecase.DeleteScheduleUseCase;
import com.clinica.salud.modules.hrm.application.usecase.ListEmployeesUseCase;
import com.clinica.salud.modules.hrm.application.usecase.ListSchedulesUseCase;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hrm/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final CreateEmployeeUseCase createEmployeeUseCase;
    private final ListEmployeesUseCase listEmployeesUseCase;
    private final DeactivateEmployeeUseCase deactivateEmployeeUseCase;
    private final CreateScheduleUseCase createScheduleUseCase;
    private final ListSchedulesUseCase listSchedulesUseCase;
    private final DeleteScheduleUseCase deleteScheduleUseCase;

    // ── Empleados ─────────────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAuthority('RR_HH_WRITE')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(@Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Empleado registrado exitosamente", createEmployeeUseCase.execute(request)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('RR_HH_READ')")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> list(
            @RequestParam(required = false) UUID sedeId,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        return ResponseEntity.ok(ApiResponse.ok("Empleados obtenidos", listEmployeesUseCase.execute(sedeId, activeOnly)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('RR_HH_WRITE')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        deactivateEmployeeUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.ok("Empleado desactivado", null));
    }

    // ── Horarios ──────────────────────────────────────────────────────────────

    @PostMapping("/schedules")
    @PreAuthorize("hasAuthority('RR_HH_WRITE')")
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(
            @Valid @RequestBody CreateScheduleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Horario registrado", createScheduleUseCase.execute(request)));
    }

    @GetMapping("/{employeeId}/schedules")
    @PreAuthorize("hasAuthority('RR_HH_READ')")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> listSchedules(
            @PathVariable UUID employeeId,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        return ResponseEntity.ok(ApiResponse.ok("Horarios obtenidos", listSchedulesUseCase.execute(employeeId, activeOnly)));
    }

    @DeleteMapping("/schedules/{scheduleId}")
    @PreAuthorize("hasAuthority('RR_HH_WRITE')")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable UUID scheduleId) {
        deleteScheduleUseCase.execute(scheduleId);
        return ResponseEntity.ok(ApiResponse.ok("Horario eliminado", null));
    }
}
