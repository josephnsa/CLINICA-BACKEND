package com.clinica.salud.modules.exam.infrastructure.persistence;

import com.clinica.salud.modules.exam.domain.model.ExamOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "exam_order_items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamOrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private ExamOrderEntity order;

    @Column(name = "service_id", nullable = false)
    private UUID serviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExamOrderStatus status;

    @Column(name = "result_text", columnDefinition = "TEXT")
    private String resultText;

    @Column(name = "result_at")
    private OffsetDateTime resultAt;

    @Column(name = "result_by")
    private UUID resultBy;
}

