package com.clinica.salud.modules.billing.application.usecase;

import com.clinica.salud.modules.billing.application.dto.CashRegisterSummaryResponse;
import com.clinica.salud.modules.billing.domain.model.PaymentMethod;
import com.clinica.salud.modules.billing.domain.port.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCashRegisterSummaryUseCase {

    private final PaymentRepository paymentRepository;

    public CashRegisterSummaryResponse execute(UUID sedeId, LocalDate date) {
        Map<PaymentMethod, BigDecimal> totalByMethod = paymentRepository.getTotalByMethodForSedeAndDate(sedeId, date);
        long invoiceCount = paymentRepository.countInvoicesWithPaymentsForSedeAndDate(sedeId, date);

        BigDecimal grandTotal = totalByMethod.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<PaymentMethod, BigDecimal> filled = new EnumMap<>(PaymentMethod.class);
        for (PaymentMethod m : PaymentMethod.values()) {
            filled.put(m, totalByMethod.getOrDefault(m, BigDecimal.ZERO));
        }
        return new CashRegisterSummaryResponse(invoiceCount, filled, grandTotal);
    }
}
