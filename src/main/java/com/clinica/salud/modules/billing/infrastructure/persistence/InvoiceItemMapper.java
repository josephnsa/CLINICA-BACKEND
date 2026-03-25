package com.clinica.salud.modules.billing.infrastructure.persistence;

import com.clinica.salud.modules.billing.domain.model.InvoiceItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvoiceItemMapper {

    InvoiceItem toDomain(InvoiceItemEntity entity);

    InvoiceItemEntity toEntity(InvoiceItem domain);
}
