package com.clinica.salud.modules.agenda.infrastructure.web;

import com.clinica.salud.modules.agenda.application.dto.AppointmentResponse;
import com.clinica.salud.modules.agenda.application.dto.AvailabilityRequest;
import com.clinica.salud.modules.agenda.application.dto.CreateAppointmentRequest;
import com.clinica.salud.modules.agenda.application.dto.TimeSlotDto;
import com.clinica.salud.modules.agenda.application.usecase.CancelAppointmentUseCase;
import com.clinica.salud.modules.agenda.application.usecase.CreateAppointmentUseCase;
import com.clinica.salud.modules.agenda.application.usecase.GetAvailabilityUseCase;
import com.clinica.salud.modules.agenda.application.usecase.GetAppointmentsByPatientUseCase;
import com.clinica.salud.modules.agenda.domain.model.Appointment;
import com.clinica.salud.modules.agenda.infrastructure.persistence.AppointmentMapper;
import com.clinica.salud.shared.exception.UnauthorizedException;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final CancelAppointmentUseCase cancelAppointmentUseCase;
    private final GetAvailabilityUseCase getAvailabilityUseCase;
    private final GetAppointmentsByPatientUseCase getAppointmentsByPatientUseCase;
    private final AppointmentMapper appointmentMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('agenda:create')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request) {
        UUID createdBy = getCurrentUserId();
        AppointmentResponse response = createAppointmentUseCase.execute(request, createdBy);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('agenda:cancel')")
    public ResponseEntity<ApiResponse<Void>> cancelAppointment(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "") String reason) {
        cancelAppointmentUseCase.execute(id, reason);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/availability")
    @PreAuthorize("hasAuthority('agenda:availability')")
    public ResponseEntity<ApiResponse<List<TimeSlotDto>>> getAvailability(
            @RequestParam UUID doctorId,
            @RequestParam UUID sedeId,
            @RequestParam LocalDate date) {
        AvailabilityRequest req = new AvailabilityRequest(doctorId, sedeId, date);
        List<TimeSlotDto> slots = getAvailabilityUseCase.execute(req);
        return ResponseEntity.ok(ApiResponse.ok(slots));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('agenda:read')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getByPatient(
            @PathVariable UUID patientId,
            @PageableDefault(size = 20) Pageable pageable) {
        List<Appointment> appointments = getAppointmentsByPatientUseCase.execute(patientId, pageable);
        List<AppointmentResponse> responses = appointments.stream()
                .map(appointmentMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    private static UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof String)) {
            throw new UnauthorizedException("Usuario no autenticado");
        }
        try {
            return UUID.fromString(auth.getPrincipal().toString());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Identidad de usuario inválida");
        }
    }
}
