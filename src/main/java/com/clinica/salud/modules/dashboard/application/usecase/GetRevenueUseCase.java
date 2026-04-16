package com.clinica.salud.modules.dashboard.application.usecase;

import com.clinica.salud.modules.dashboard.application.dto.RevenueMonthResponse;
import com.clinica.salud.modules.dashboard.domain.port.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetRevenueUseCase {

    private final DashboardRepository dashboardRepository;

    @Transactional(readOnly = true)
    public List<RevenueMonthResponse> execute(UUID sedeId, int year) {
        return dashboardRepository.getMonthlyRevenue(sedeId, year);
    }
}
