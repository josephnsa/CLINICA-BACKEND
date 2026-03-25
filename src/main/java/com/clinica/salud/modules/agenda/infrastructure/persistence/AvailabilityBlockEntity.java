package com.clinica.salud.modules.agenda.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "availability_blocks")
@Data
public class AvailabilityBlockEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @Column(name = "doctor_id")
    private UUID doctorId;

    @Column(name = "sede_id")
    private UUID sedeId;

    @Column(name = "start_dt", nullable = false)
    private LocalDateTime startDt;

    @Column(name = "end_dt", nullable = false)
    private LocalDateTime endDt;

    @Column(name = "reason", length = 200)
    private String reason;
}
