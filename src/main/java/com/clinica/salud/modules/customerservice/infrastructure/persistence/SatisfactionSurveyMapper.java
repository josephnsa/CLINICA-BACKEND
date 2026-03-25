package com.clinica.salud.modules.customerservice.infrastructure.persistence;

import com.clinica.salud.modules.customerservice.domain.model.SatisfactionSurvey;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SatisfactionSurveyMapper {

    SatisfactionSurvey toDomain(SatisfactionSurveyEntity entity);

    SatisfactionSurveyEntity toEntity(SatisfactionSurvey domain);
}
