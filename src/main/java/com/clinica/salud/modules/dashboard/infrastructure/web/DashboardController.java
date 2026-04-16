package com.clinica.salud.modules.dashboard.infrastructure.web;

import com.clinica.salud.modules.dashboard.application.dto.*;
import com.clinica.salud.modules.dashboard.application.usecase.*;
import com.clinica.salud.shared.exception.UnauthorizedException;
import com.clinica.salud.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final GetDashboardSummaryUseCase    summaryUseCase;
    private final GetRevenueUseCase             revenueUseCase;
    private final GetYearlyBreakupUseCase       yearlyBreakupUseCase;
    private final GetRecentTransactionsUseCase  transactionsUseCase;
    private final GetServicePerformanceUseCase  performanceUseCase;
    private final GetNotificationsUseCase       notificationsUseCase;
    private final GetHeaderUserUseCase          headerUserUseCase;

    /**
     * KPIs principales: ingresos del mes, % cambio, ingresos del año, citas, pacientes nuevos.
     * Widget: Monthly Earnings + Yearly Breakup (cifras resumen)
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('REPORTES_READ')")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> summary(
            @RequestParam UUID sedeId,
            @RequestParam(required = false) Integer year) {
        int y = year != null ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(ApiResponse.ok(summaryUseCase.execute(sedeId, y)));
    }

    /**
     * Ingresos mes a mes para el año solicitado (12 elementos).
     * Widget: Revenue Updates (gráfico de barras)
     */
    @GetMapping("/revenue")
    @PreAuthorize("hasAuthority('REPORTES_READ')")
    public ResponseEntity<ApiResponse<List<RevenueMonthResponse>>> revenue(
            @RequestParam UUID sedeId,
            @RequestParam(required = false) Integer year) {
        int y = year != null ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(ApiResponse.ok(revenueUseCase.execute(sedeId, y)));
    }

    /**
     * Ingresos de los últimos 3 años con % de crecimiento.
     * Widget: Yearly Breakup (donut chart)
     */
    @GetMapping("/yearly-breakup")
    @PreAuthorize("hasAuthority('REPORTES_READ')")
    public ResponseEntity<ApiResponse<YearlyBreakupResponse>> yearlyBreakup(
            @RequestParam UUID sedeId) {
        return ResponseEntity.ok(ApiResponse.ok(yearlyBreakupUseCase.execute(sedeId)));
    }

    /**
     * Últimas N transacciones de pago ordenadas por fecha desc.
     * Widget: Recent Transactions
     */
    @GetMapping("/recent-transactions")
    @PreAuthorize("hasAuthority('REPORTES_READ')")
    public ResponseEntity<ApiResponse<List<RecentTransactionResponse>>> recentTransactions(
            @RequestParam UUID sedeId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(transactionsUseCase.execute(sedeId, limit)));
    }

    /**
     * Rendimiento de médicos/servicios con badge de prioridad calculado por percentil.
     * Widget: Product Performance (tabla)
     */
    @GetMapping("/performance")
    @PreAuthorize("hasAuthority('REPORTES_READ')")
    public ResponseEntity<ApiResponse<List<ServicePerformanceResponse>>> performance(
            @RequestParam UUID sedeId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        int m = month != null ? month : LocalDate.now().getMonthValue();
        int y = year  != null ? year  : LocalDate.now().getYear();
        return ResponseEntity.ok(ApiResponse.ok(performanceUseCase.execute(sedeId, m, y)));
    }

    /**
     * Notificaciones del header: citas hoy, resultados, facturas pendientes, stock bajo.
     * Header: campana con badge de conteo total.
     */
    @GetMapping("/notifications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<NotificationSummaryResponse>> notifications(
            @RequestParam UUID sedeId) {
        return ResponseEntity.ok(ApiResponse.ok(notificationsUseCase.execute(sedeId)));
    }

    /**
     * Información del usuario actual para el header: nombre, rol, sede, iniciales, sedes disponibles.
     * Header: avatar + nombre + selector de sede.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<HeaderUserResponse>> me() {
        return ResponseEntity.ok(ApiResponse.ok(headerUserUseCase.execute(getCurrentUserId())));
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof String)) {
            throw new UnauthorizedException("Usuario no autenticado");
        }
        try {
            return UUID.fromString(auth.getPrincipal().toString());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Identidad de usuario inválida");
        }
    }
}
