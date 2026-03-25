package com.clinica.salud.modules.billing.infrastructure.persistence;

import com.clinica.salud.modules.billing.domain.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceJpaRepository extends JpaRepository<InvoiceEntity, UUID> {

    @Query("SELECT COALESCE(MAX(i.number), 0) + 1 FROM InvoiceEntity i WHERE i.serie = :serie")
    int getNextNumberForSerie(@Param("serie") String serie);
}
