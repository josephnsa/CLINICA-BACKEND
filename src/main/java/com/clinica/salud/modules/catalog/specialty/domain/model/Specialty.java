package com.clinica.salud.modules.catalog.specialty.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Specialty {

    private UUID id;
    private String code;
    private String name;
    private boolean isActive;
}

