package com.clinica.salud.modules.customerservice.application.dto;

import com.clinica.salud.modules.customerservice.domain.model.ComplaintPriority;
import com.clinica.salud.modules.customerservice.domain.model.ComplaintType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateComplaintRequest {

    private UUID patientId;

    @NotNull(message = "La sede es obligatoria")
    private UUID sedeId;

    @NotNull(message = "El tipo es obligatorio")
    private ComplaintType type;

    @NotBlank(message = "El asunto es obligatorio")
    private String subject;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    private ComplaintPriority priority = ComplaintPriority.MEDIUM;
}
