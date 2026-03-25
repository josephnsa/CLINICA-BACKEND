package com.clinica.salud.modules.billing.infrastructure.persistence;

import com.clinica.salud.modules.billing.domain.model.Payment;
import com.clinica.salud.modules.billing.domain.model.PaymentMethod;
import com.clinica.salud.modules.billing.domain.port.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = paymentMapper.toEntity(payment);
        entity = paymentJpaRepository.save(entity);
        payment.setId(entity.getId());
        return payment;
    }

    @Override
    public List<Payment> findByInvoiceId(UUID invoiceId) {
        return paymentJpaRepository.findByInvoiceId(invoiceId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public BigDecimal getTotalPaidForInvoice(UUID invoiceId) {
        BigDecimal sum = paymentJpaRepository.sumAmountByInvoiceId(invoiceId);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public Map<PaymentMethod, BigDecimal> getTotalByMethodForSedeAndDate(UUID sedeId, LocalDate date) {
        List<Object[]> rows = paymentJpaRepository.sumAmountByMethodForSedeAndDate(sedeId, date);
        Map<PaymentMethod, BigDecimal> result = new EnumMap<>(PaymentMethod.class);
        for (PaymentMethod m : PaymentMethod.values()) {
            result.put(m, BigDecimal.ZERO);
        }
        for (Object[] row : rows) {
            PaymentMethod method = (PaymentMethod) row[0];
            BigDecimal total = (BigDecimal) row[1];
            result.put(method, total != null ? total : BigDecimal.ZERO);
        }
        return result;
    }

    @Override
    public long countInvoicesWithPaymentsForSedeAndDate(UUID sedeId, LocalDate date) {
        return paymentJpaRepository.countDistinctInvoicesWithPaymentsForSedeAndDate(sedeId, date);
    }

    private Payment toDomain(PaymentEntity e) {
        return Payment.builder()
                .id(e.getId())
                .invoiceId(e.getInvoiceId())
                .method(e.getMethod())
                .amount(e.getAmount())
                .reference(e.getReference())
                .paidAt(e.getPaidAt())
                .createdBy(e.getCreatedBy())
                .build();
    }
}
