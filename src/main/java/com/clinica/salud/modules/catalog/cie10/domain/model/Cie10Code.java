package com.clinica.salud.modules.catalog.cie10.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cie10Code {

    private String code;
    private String description;
    private String category;
}

