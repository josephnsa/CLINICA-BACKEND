package com.clinica.salud.modules.agenda.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "doctor_availability")
@Data
public class DoctorAvailabilityEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @Column(name = "doctor_id")
    private UUID doctorId;

    @Column(name = "sede_id")
    private UUID sedeId;

    @Column(name = "day_of_week", nullable = false)
    private int dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
