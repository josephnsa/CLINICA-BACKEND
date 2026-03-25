package com.clinica.salud.modules.exam.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamOrderItem {

    private UUID id;
    private UUID serviceId;
    private ExamOrderStatus status;
    private String resultText;
    private OffsetDateTime resultAt;
    private UUID resultBy;
}

