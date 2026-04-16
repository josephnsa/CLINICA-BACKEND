package com.clinica.salud.modules.patientportal.infrastructure.web;

import com.clinica.salud.modules.patientportal.application.dto.PortalAppointmentResponse;
import com.clinica.salud.modules.patientportal.application.dto.PortalExamResponse;
import com.clinica.salud.modules.patientportal.application.dto.PortalPrescriptionResponse;
import com.clinica.salud.modules.patientportal.application.usecase.GetMyAppointmentsUseCase;
import com.clinica.salud.modules.patientportal.application.usecase.GetMyExamResultsUseCase;
import com.clinica.salud.modules.patientportal.application.usecase.GetMyPrescriptionsUseCase;
import com.clinica.salud.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
public class PatientPortalController {

    private final GetMyAppointmentsUseCase getMyAppointmentsUseCase;
    private final GetMyExamResultsUseCase getMyExamResultsUseCase;
    private final GetMyPrescriptionsUseCase getMyPrescriptionsUseCase;

    /**
     * Retorna las citas del paciente autenticado.
     * El subject del JWT es el patientId.
     */
    @GetMapping("/appointments")
    @PreAuthorize("hasRole('PACIENTE_PORTAL')")
    public ResponseEntity<ApiResponse<List<PortalAppointmentResponse>>> myAppointments(
            @AuthenticationPrincipal String patientId) {
        return ResponseEntity.ok(ApiResponse.ok(
                getMyAppointmentsUseCase.execute(UUID.fromString(patientId))));
    }

    /**
     * Retorna los resultados de exámenes del paciente autenticado.
     */
    @GetMapping("/exams")
    @PreAuthorize("hasRole('PACIENTE_PORTAL')")
    public ResponseEntity<ApiResponse<List<PortalExamResponse>>> myExams(
            @AuthenticationPrincipal String patientId) {
        return ResponseEntity.ok(ApiResponse.ok(
                getMyExamResultsUseCase.execute(UUID.fromString(patientId))));
    }

    /**
     * Retorna las prescripciones del paciente autenticado.
     */
    @GetMapping("/prescriptions")
    @PreAuthorize("hasRole('PACIENTE_PORTAL')")
    public ResponseEntity<ApiResponse<List<PortalPrescriptionResponse>>> myPrescriptions(
            @AuthenticationPrincipal String patientId) {
        return ResponseEntity.ok(ApiResponse.ok(
                getMyPrescriptionsUseCase.execute(UUID.fromString(patientId))));
    }
}
