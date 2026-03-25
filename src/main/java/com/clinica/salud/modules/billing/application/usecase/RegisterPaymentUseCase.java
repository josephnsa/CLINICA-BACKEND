package com.clinica.salud.modules.billing.application.usecase;

import com.clinica.salud.modules.billing.application.dto.InvoiceResponse;
import com.clinica.salud.modules.billing.application.dto.RegisterPaymentRequest;
import com.clinica.salud.modules.billing.domain.model.Invoice;
import com.clinica.salud.modules.billing.domain.model.InvoiceStatus;
import com.clinica.salud.modules.billing.domain.model.Payment;
import com.clinica.salud.modules.billing.domain.port.InvoiceRepository;
import com.clinica.salud.modules.billing.domain.port.PaymentRepository;
import com.clinica.salud.shared.exception.BusinessRuleException;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterPaymentUseCase {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public InvoiceResponse execute(RegisterPaymentRequest request, UUID createdBy) {
        Invoice invoice = invoiceRepository.findById(request.invoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", request.invoiceId().toString()));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessRuleException("La factura ya está pagada");
        }
        if (invoice.getStatus() == InvoiceStatus.CANCELLED || invoice.getStatus() == InvoiceStatus.REFUNDED) {
            throw new BusinessRuleException("No se pueden registrar pagos en una factura cancelada o reembolsada");
        }

        java.math.BigDecimal totalPaid = paymentRepository.getTotalPaidForInvoice(invoice.getId());
        java.math.BigDecimal pending = invoice.getTotal().subtract(totalPaid);
        if (request.amount().compareTo(pending) > 0) {
            throw new BusinessRuleException("El pago no puede exceder el saldo pendiente: " + pending);
        }

        LocalDateTime now = LocalDateTime.now();
        Payment payment = Payment.builder()
                .invoiceId(request.invoiceId())
                .method(request.method())
                .amount(request.amount())
                .reference(request.reference() != null ? request.reference() : "")
                .paidAt(now)
                .createdBy(createdBy)
                .build();
        paymentRepository.save(payment);

        totalPaid = totalPaid.add(request.amount());
        if (totalPaid.compareTo(invoice.getTotal()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
        }

        invoice = invoiceRepository.findById(invoice.getId()).orElseThrow();
        return InvoiceResponse.from(invoice);
    }
}
