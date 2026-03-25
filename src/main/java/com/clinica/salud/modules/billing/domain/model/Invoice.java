package com.clinica.salud.modules.billing.domain.model;

import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {

    private UUID id;
    private String serie;
    private Integer number;
    private UUID patientId;
    private UUID sedeId;
    private UUID appointmentId;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private InvoiceStatus status;
    private String notes;
    private UUID createdBy;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();

    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    /**
     * Recalcula subtotal, impuesto (18% IGV) y total a partir de los ítems.
     */
    public void recalculateTotals() {
        this.subtotal = items.stream()
                .map(InvoiceItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.tax = this.subtotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        this.total = this.subtotal.add(this.tax).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Monto total ya pagado.
     */
    public BigDecimal getPaidAmount() {
        if (payments == null) return BigDecimal.ZERO;
        return payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Saldo pendiente de pago.
     */
    public BigDecimal getRemainingBalance() {
        return total == null ? BigDecimal.ZERO : total.subtract(getPaidAmount());
    }

    /**
     * Indica si la factura está completamente pagada.
     */
    public boolean isFullyPaid() {
        return getRemainingBalance().compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * Registra un pago y actualiza el estado a PAID si queda saldada.
     */
    public void addPayment(Payment payment) {
        if (this.status == InvoiceStatus.CANCELLED || this.status == InvoiceStatus.REFUNDED) {
            throw new BusinessRuleException("No se puede registrar un pago en una factura " + this.status);
        }
        if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("El monto del pago debe ser mayor a cero");
        }
        if (payment.getAmount().compareTo(getRemainingBalance()) > 0) {
            throw new BusinessRuleException("El pago excede el saldo pendiente de S/ " + getRemainingBalance());
        }
        this.payments.add(payment);
        if (isFullyPaid()) {
            this.status = InvoiceStatus.PAID;
        }
    }

    /**
     * Cancela la factura. Solo si está PENDING o DRAFT.
     */
    public void cancel() {
        if (this.status != InvoiceStatus.PENDING && this.status != InvoiceStatus.DRAFT) {
            throw new BusinessRuleException("Solo se puede cancelar una factura en estado PENDING o DRAFT");
        }
        this.status = InvoiceStatus.CANCELLED;
    }

    /**
     * Anula/devuelve la factura (nota de crédito). Solo si está PAID.
     */
    public void refund() {
        if (this.status != InvoiceStatus.PAID) {
            throw new BusinessRuleException("Solo se puede anular una factura PAID");
        }
        this.status = InvoiceStatus.REFUNDED;
    }

    /**
     * Indica si esta factura es una proforma/presupuesto.
     */
    public boolean isProforma() {
        return this.status == InvoiceStatus.DRAFT;
    }

    /**
     * Confirma una proforma convirtiéndola en factura real (PENDING).
     */
    public void confirmProforma() {
        if (this.status != InvoiceStatus.DRAFT) {
            throw new BusinessRuleException("Solo se puede confirmar una factura en estado DRAFT (proforma)");
        }
        if (items == null || items.isEmpty()) {
            throw new BusinessRuleException("La factura debe tener al menos un ítem");
        }
        this.status = InvoiceStatus.PENDING;
    }
}
