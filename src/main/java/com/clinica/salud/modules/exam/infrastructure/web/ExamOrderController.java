package com.clinica.salud.modules.exam.infrastructure.web;

import com.clinica.salud.modules.exam.application.dto.CreateExamOrderRequest;
import com.clinica.salud.modules.exam.application.dto.ExamOrderResponse;
import com.clinica.salud.modules.exam.application.dto.RegisterExamResultRequest;
import com.clinica.salud.modules.exam.application.usecase.CreateExamOrderUseCase;
import com.clinica.salud.modules.exam.application.usecase.GetExamOrdersByPatientUseCase;
import com.clinica.salud.modules.exam.application.usecase.RegisterExamResultUseCase;
import com.clinica.salud.modules.exam.application.usecase.SignExamOrderUseCase;
import com.clinica.salud.modules.integration.pdf.PdfService;
import com.clinica.salud.shared.exception.UnauthorizedException;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ExamOrderController {

    private final CreateExamOrderUseCase createExamOrderUseCase;
    private final GetExamOrdersByPatientUseCase getExamOrdersByPatientUseCase;
    private final RegisterExamResultUseCase registerExamResultUseCase;
    private final SignExamOrderUseCase signExamOrderUseCase;
    private final PdfService pdfService;

    @PostMapping("/api/exams/orders")
    @PreAuthorize("hasAuthority('EXAMENES_CREATE')")
    public ResponseEntity<ApiResponse<ExamOrderResponse>> create(
            @Valid @RequestBody CreateExamOrderRequest request) {
        UUID userId = getCurrentUserId();
        ExamOrderResponse response = createExamOrderUseCase.execute(request, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/exams/orders")
    @PreAuthorize("hasAuthority('EXAMENES_READ')")
    public ResponseEntity<ApiResponse<List<ExamOrderResponse>>> getByPatient(
            @RequestParam UUID patientId) {
        List<ExamOrderResponse> response = getExamOrdersByPatientUseCase.execute(patientId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/api/exams/orders/{id}/sign")
    @PreAuthorize("hasAuthority('EXAMENES_SIGN')")
    public ResponseEntity<ApiResponse<ExamOrderResponse>> sign(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.ok(signExamOrderUseCase.execute(id, userId)));
    }

    @GetMapping("/api/exams/orders/{id}/pdf")
    @PreAuthorize("hasAuthority('EXAMENES_READ')")
    public ResponseEntity<byte[]> downloadExamOrderPdf(@PathVariable UUID id) {
        byte[] pdf = pdfService.generateExamOrderPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"exam-order-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/api/exams/orders/{id}/result")
    @PreAuthorize("hasAuthority('EXAMENES_RESULT')")
    public ResponseEntity<ApiResponse<ExamOrderResponse>> registerResult(
            @PathVariable UUID id,
            @Valid @RequestBody RegisterExamResultRequest request) {
        UUID userId = getCurrentUserId();
        ExamOrderResponse response = registerExamResultUseCase.execute(id, request, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
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

