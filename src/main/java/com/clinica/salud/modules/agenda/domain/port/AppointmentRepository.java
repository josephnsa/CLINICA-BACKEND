package com.clinica.salud.modules.agenda.domain.port;

import com.clinica.salud.modules.agenda.domain.model.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

public interface AppointmentRepository {

    Appointment save(Appointment appointment);

    Optional<Appointment> findById(UUID id);

    List<Appointment> findByDoctorAndDate(UUID doctorId, LocalDate date);

    List<Appointment> findByPatientId(UUID patientId, Pageable pageable);

    List<Appointment> findAll(Pageable pageable);

    boolean hasConflict(UUID doctorId, LocalDateTime start, LocalDateTime end, UUID excludeId);

    List<Appointment> findBySedeAndDateRange(UUID sedeId, LocalDateTime start, LocalDateTime end);
}
