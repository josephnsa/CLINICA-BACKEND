package com.clinica.salud.modules.prescription.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionItem {

    private UUID id;
    private UUID medicationId;
    private String dose;
    private String frequency;
    private String duration;
    private String route;
    private String instructions;
    private int quantity;
    private boolean dispensed;
}

