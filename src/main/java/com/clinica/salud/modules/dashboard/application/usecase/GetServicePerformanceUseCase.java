package com.clinica.salud.modules.dashboard.application.usecase;

import com.clinica.salud.modules.dashboard.application.dto.ServicePerformanceResponse;
import com.clinica.salud.modules.dashboard.domain.port.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetServicePerformanceUseCase {

    private final DashboardRepository dashboardRepository;

    @Transactional(readOnly = true)
    public List<ServicePerformanceResponse> execute(UUID sedeId, int month, int year) {
        List<ServicePerformanceResponse> rows = dashboardRepository.getServicePerformanceRaw(sedeId, month, year);
        if (rows.isEmpty()) return rows;

        // Calcular percentiles sobre los ingresos generados
        long[] revenues = rows.stream()
                .mapToLong(r -> r.revenueGenerated().longValue())
                .sorted()
                .toArray();

        long p75 = percentile(revenues, 75);
        long p50 = percentile(revenues, 50);

        return rows.stream()
                .map(r -> new ServicePerformanceResponse(
                        r.doctorId(),
                        r.doctorName(),
                        r.serviceName(),
                        r.appointmentCount(),
                        r.revenueGenerated(),
                        assignPriority(r.appointmentCount(), r.revenueGenerated().longValue(), p50, p75)
                ))
                .collect(Collectors.toList());
    }

    private String assignPriority(long appointmentCount, long revenue, long p50, long p75) {
        if (appointmentCount == 0)  return "CRITICAL";
        if (revenue > p75)          return "HIGH";
        if (revenue > p50)          return "MEDIUM";
        return "LOW";
    }

    private long percentile(long[] sorted, int pct) {
        int idx = (int) Math.ceil((pct / 100.0) * sorted.length) - 1;
        return sorted[Math.max(0, Math.min(idx, sorted.length - 1))];
    }
}
