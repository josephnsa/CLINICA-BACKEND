package com.clinica.salud.modules.billing.infrastructure.persistence;

import com.clinica.salud.modules.billing.domain.model.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "payments", ignore = true)
    Invoice toDomain(InvoiceEntity entity);

    InvoiceEntity toEntity(Invoice domain);
}
