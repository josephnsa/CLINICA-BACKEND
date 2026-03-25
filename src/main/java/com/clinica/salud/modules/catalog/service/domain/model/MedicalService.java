package com.clinica.salud.modules.catalog.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicalService {

    private UUID id;
    private String code;
    private String name;
    private UUID specialtyId;
    private int durationMin;
    private BigDecimal price;
    private boolean isActive;
}

