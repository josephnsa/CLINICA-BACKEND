package com.clinica.salud.modules.customerservice.application.dto;

import com.clinica.salud.modules.customerservice.domain.model.ComplaintPriority;
import com.clinica.salud.modules.customerservice.domain.model.ComplaintStatus;
import com.clinica.salud.modules.customerservice.domain.model.ComplaintType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ComplaintResponse {

    private UUID id;
    private UUID patientId;
    private UUID sedeId;
    private ComplaintType type;
    private String subject;
    private String description;
    private ComplaintStatus status;
    private ComplaintPriority priority;
    private boolean isUrgent;
    private UUID assignedTo;
    private LocalDateTime resolvedAt;
    private String resolution;
    private LocalDateTime createdAt;
}
