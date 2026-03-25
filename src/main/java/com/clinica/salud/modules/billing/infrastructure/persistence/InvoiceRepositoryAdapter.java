package com.clinica.salud.modules.billing.infrastructure.persistence;

import com.clinica.salud.modules.billing.domain.model.Invoice;
import com.clinica.salud.modules.billing.domain.model.InvoiceItem;
import com.clinica.salud.modules.billing.domain.port.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InvoiceRepositoryAdapter implements InvoiceRepository {

    private final InvoiceJpaRepository invoiceJpaRepository;
    private final InvoiceItemJpaRepository invoiceItemJpaRepository;
    private final PaymentJpaRepository paymentJpaRepository;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceItemMapper invoiceItemMapper;
    private final PaymentMapper paymentMapper;

    @Override
    public Invoice save(Invoice invoice) {
        InvoiceEntity entity = invoiceMapper.toEntity(invoice);
        entity = invoiceJpaRepository.save(entity);
        invoice.setId(entity.getId());

        if (invoice.getItems() != null) {
            for (InvoiceItem item : invoice.getItems()) {
                InvoiceItemEntity itemEntity = invoiceItemMapper.toEntity(item);
                itemEntity.setInvoiceId(entity.getId());
                itemEntity = invoiceItemJpaRepository.save(itemEntity);
                item.setId(itemEntity.getId());
            }
        }
        invoice.getPayments().clear();
        invoice.getPayments().addAll(
                paymentJpaRepository.findByInvoiceId(entity.getId()).stream()
                        .map(paymentMapper::toDomain).toList());
        return invoice;
    }

    @Override
    public Optional<Invoice> findById(UUID id) {
        return invoiceJpaRepository.findById(id).map(entity -> {
            Invoice domain = invoiceMapper.toDomain(entity);
            List<InvoiceItemEntity> itemEntities = invoiceItemJpaRepository.findByInvoiceId(id);
            domain.getItems().addAll(itemEntities.stream().map(invoiceItemMapper::toDomain).toList());
            List<PaymentEntity> paymentEntities = paymentJpaRepository.findByInvoiceId(id);
            domain.getPayments().addAll(paymentEntities.stream().map(paymentMapper::toDomain).toList());
            return domain;
        });
    }

    @Override
    public int getNextNumberForSerie(String serie) {
        return invoiceJpaRepository.getNextNumberForSerie(serie);
    }
}
