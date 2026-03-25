package com.clinica.salud.modules.billing.infrastructure.persistence;

import com.clinica.salud.modules.billing.domain.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {

    List<PaymentEntity> findByInvoiceId(UUID invoiceId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentEntity p WHERE p.invoiceId = :invoiceId")
    BigDecimal sumAmountByInvoiceId(@Param("invoiceId") UUID invoiceId);

    @Query("SELECT p.method, SUM(p.amount) FROM PaymentEntity p " +
            "JOIN InvoiceEntity i ON i.id = p.invoiceId " +
            "WHERE i.sedeId = :sedeId AND CAST(p.paidAt AS date) = :date " +
            "GROUP BY p.method")
    List<Object[]> sumAmountByMethodForSedeAndDate(@Param("sedeId") UUID sedeId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(DISTINCT p.invoiceId) FROM PaymentEntity p " +
            "JOIN InvoiceEntity i ON i.id = p.invoiceId " +
            "WHERE i.sedeId = :sedeId AND CAST(p.paidAt AS date) = :date")
    long countDistinctInvoicesWithPaymentsForSedeAndDate(@Param("sedeId") UUID sedeId, @Param("date") LocalDate date);
}
