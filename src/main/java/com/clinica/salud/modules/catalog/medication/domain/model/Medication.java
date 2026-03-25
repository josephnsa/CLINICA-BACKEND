package com.clinica.salud.modules.catalog.medication.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Medication {

    private UUID id;
    private String code;
    private String genericName;
    private String commercialName;
    private String presentation;
    private String unit;
    private boolean isActive;
}

