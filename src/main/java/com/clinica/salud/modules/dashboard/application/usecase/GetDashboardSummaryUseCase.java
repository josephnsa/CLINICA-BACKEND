package com.clinica.salud.modules.dashboard.application.usecase;

import com.clinica.salud.modules.dashboard.application.dto.DashboardSummaryResponse;
import com.clinica.salud.modules.dashboard.domain.port.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetDashboardSummaryUseCase {

    private final DashboardRepository dashboardRepository;

    @Transactional(readOnly = true)
    public DashboardSummaryResponse execute(UUID sedeId, int year) {
        int month = LocalDate.now().getMonthValue();
        return dashboardRepository.getSummary(sedeId, year, month);
    }
}
