package com.clinica.salud.modules.dashboard.application.usecase;

import com.clinica.salud.modules.dashboard.application.dto.YearlyBreakupResponse;
import com.clinica.salud.modules.dashboard.domain.port.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetYearlyBreakupUseCase {

    private final DashboardRepository dashboardRepository;

    @Transactional(readOnly = true)
    public YearlyBreakupResponse execute(UUID sedeId) {
        return dashboardRepository.getYearlyBreakup(sedeId);
    }
}
