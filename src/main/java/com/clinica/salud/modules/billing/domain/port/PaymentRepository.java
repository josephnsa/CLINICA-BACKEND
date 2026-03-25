package com.clinica.salud.modules.billing.domain.port;

import com.clinica.salud.modules.billing.domain.model.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.clinica.salud.modules.billing.domain.model.PaymentMethod;

public interface PaymentRepository {

    Payment save(Payment payment);

    List<Payment> findByInvoiceId(UUID invoiceId);

    BigDecimal getTotalPaidForInvoice(UUID invoiceId);

    /**
     * Total cobrado por método de pago para una sede y fecha.
     */
    Map<PaymentMethod, BigDecimal> getTotalByMethodForSedeAndDate(UUID sedeId, LocalDate date);

    /**
     * Cantidad de facturas con al menos un pago en la sede y fecha.
     */
    long countInvoicesWithPaymentsForSedeAndDate(UUID sedeId, LocalDate date);
}
