package com.clinica.salud.modules.hrm.infrastructure.web;

import com.clinica.salud.modules.hrm.application.dto.AttendanceResponse;
import com.clinica.salud.modules.hrm.application.dto.ProductivityReport;
import com.clinica.salud.modules.hrm.application.dto.RegisterCheckInRequest;
import com.clinica.salud.modules.hrm.application.dto.RegisterCheckOutRequest;
import com.clinica.salud.modules.hrm.application.usecase.GetProductivityReportUseCase;
import com.clinica.salud.modules.hrm.application.usecase.ListAttendanceUseCase;
import com.clinica.salud.modules.hrm.application.usecase.RegisterCheckInUseCase;
import com.clinica.salud.modules.hrm.application.usecase.RegisterCheckOutUseCase;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hrm/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final RegisterCheckInUseCase registerCheckInUseCase;
    private final RegisterCheckOutUseCase registerCheckOutUseCase;
    private final ListAttendanceUseCase listAttendanceUseCase;
    private final GetProductivityReportUseCase getProductivityReportUseCase;

    @PostMapping("/checkin")
    @PreAuthorize("hasAuthority('RR_HH_WRITE')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn(
            @Valid @RequestBody RegisterCheckInRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Entrada registrada", registerCheckInUseCase.execute(request)));
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('RR_HH_WRITE')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkOut(
            @Valid @RequestBody RegisterCheckOutRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Salida registrada", registerCheckOutUseCase.execute(request)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('RR_HH_READ')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> list(
            @RequestParam UUID employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.ok(listAttendanceUseCase.execute(employeeId, from, to)));
    }

    @GetMapping("/productivity/{employeeId}")
    @PreAuthorize("hasAuthority('RR_HH_READ')")
    public ResponseEntity<ApiResponse<ProductivityReport>> productivity(
            @PathVariable UUID employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.ok(getProductivityReportUseCase.execute(employeeId, from, to)));
    }
}
