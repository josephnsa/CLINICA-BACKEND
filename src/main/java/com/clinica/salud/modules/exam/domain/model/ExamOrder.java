package com.clinica.salud.modules.exam.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamOrder {

    private UUID id;
    private UUID patientId;
    private UUID doctorId;
    private UUID appointmentId;
    private ExamOrderStatus status;
    private String notes;
    private OffsetDateTime createdAt;
    private UUID createdBy;
    private List<ExamOrderItem> items;
}

