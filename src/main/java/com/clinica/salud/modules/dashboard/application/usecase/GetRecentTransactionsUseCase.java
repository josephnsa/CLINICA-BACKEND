package com.clinica.salud.modules.dashboard.application.usecase;

import com.clinica.salud.modules.dashboard.application.dto.RecentTransactionResponse;
import com.clinica.salud.modules.dashboard.domain.port.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetRecentTransactionsUseCase {

    private final DashboardRepository dashboardRepository;

    @Transactional(readOnly = true)
    public List<RecentTransactionResponse> execute(UUID sedeId, int limit) {
        return dashboardRepository.getRecentTransactions(sedeId, limit);
    }
}
