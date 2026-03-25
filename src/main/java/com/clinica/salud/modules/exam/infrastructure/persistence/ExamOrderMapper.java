package com.clinica.salud.modules.exam.infrastructure.persistence;

import com.clinica.salud.modules.exam.domain.model.ExamOrder;
import com.clinica.salud.modules.exam.domain.model.ExamOrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ExamOrderMapper {

    @Mapping(target = "items", ignore = true)
    ExamOrderEntity toEntity(ExamOrder domain);

    @Mapping(target = "items", ignore = true)
    ExamOrder toDomain(ExamOrderEntity entity);

    ExamOrderItemEntity toEntity(ExamOrderItem item);

    ExamOrderItem toDomain(ExamOrderItemEntity entity);

    @AfterMapping
    default void linkItems(@MappingTarget ExamOrderEntity entity, ExamOrder domain) {
        entity.getItems().clear();
        if (domain.getItems() != null) {
            for (ExamOrderItem item : domain.getItems()) {
                ExamOrderItemEntity itemEntity = toEntity(item);
                itemEntity.setOrder(entity);
                entity.getItems().add(itemEntity);
            }
        }
    }

    @AfterMapping
    default void linkDomainItems(@MappingTarget ExamOrder order, ExamOrderEntity entity) {
        order.setItems(entity.getItems().stream()
                .map(this::toDomain)
                .toList());
    }
}

