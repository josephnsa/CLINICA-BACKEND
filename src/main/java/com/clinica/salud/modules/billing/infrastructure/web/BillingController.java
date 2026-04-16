package com.clinica.salud.modules.billing.infrastructure.web;

import com.clinica.salud.modules.billing.application.dto.CashRegisterSummaryResponse;
import com.clinica.salud.modules.billing.application.dto.CreateInvoiceRequest;
import com.clinica.salud.modules.billing.application.dto.InvoiceResponse;
import com.clinica.salud.modules.billing.application.dto.RegisterPaymentRequest;
import com.clinica.salud.modules.billing.application.usecase.CreateInvoiceUseCase;
import com.clinica.salud.modules.billing.application.usecase.GetCashRegisterSummaryUseCase;
import com.clinica.salud.modules.billing.application.usecase.GetInvoiceUseCase;
import com.clinica.salud.modules.billing.application.usecase.RefundInvoiceUseCase;
import com.clinica.salud.modules.billing.application.usecase.RegisterPaymentUseCase;
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

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BillingController {

    private final CreateInvoiceUseCase createInvoiceUseCase;
    private final GetInvoiceUseCase getInvoiceUseCase;
    private final RegisterPaymentUseCase registerPaymentUseCase;
    private final GetCashRegisterSummaryUseCase getCashRegisterSummaryUseCase;
    private final RefundInvoiceUseCase refundInvoiceUseCase;
    private final PdfService pdfService;

    @PostMapping("/api/invoices")
    @PreAuthorize("hasAuthority('FACTURACION_CREATE')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request) {
        UUID createdBy = getCurrentUserId();
        InvoiceResponse response = createInvoiceUseCase.execute(request, createdBy);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/invoices/{id}")
    @PreAuthorize("hasAuthority('FACTURACION_READ')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoice(@PathVariable UUID id) {
        InvoiceResponse response = getInvoiceUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/invoices/cash-register-summary")
    @PreAuthorize("hasAuthority('FACTURACION_READ')")
    public ResponseEntity<ApiResponse<CashRegisterSummaryResponse>> getCashRegisterSummary(
            @RequestParam UUID sedeId,
            @RequestParam LocalDate date) {
        CashRegisterSummaryResponse response = getCashRegisterSummaryUseCase.execute(sedeId, date);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/invoices/{id}/pdf")
    @PreAuthorize("hasAuthority('FACTURACION_READ')")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable UUID id) {
        byte[] pdf = pdfService.generateInvoicePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PatchMapping("/api/invoices/{id}/refund")
    @PreAuthorize("hasAuthority('FACTURACION_REFUND')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> refund(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(refundInvoiceUseCase.execute(id)));
    }

    @PostMapping("/api/payments")
    @PreAuthorize("hasAuthority('FACTURACION_CREATE')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> registerPayment(
            @Valid @RequestBody RegisterPaymentRequest request) {
        UUID createdBy = getCurrentUserId();
        InvoiceResponse response = registerPaymentUseCase.execute(request, createdBy);
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
