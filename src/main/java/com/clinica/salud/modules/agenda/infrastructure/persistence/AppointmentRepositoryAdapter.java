package com.clinica.salud.modules.agenda.infrastructure.persistence;

import com.clinica.salud.modules.agenda.domain.model.Appointment;
import com.clinica.salud.modules.agenda.domain.port.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AppointmentRepositoryAdapter implements AppointmentRepository {

    private final AppointmentJpaRepository appointmentJpaRepository;
    private final AppointmentMapper appointmentMapper;
    private final AgendaPatientJpaRepository patientJpaRepository;
    private final DoctorJpaRepository doctorJpaRepository;
    private final AgendaServiceJpaRepository serviceJpaRepository;
    private final SedeJpaRepository sedeJpaRepository;

    @Override
    public Appointment save(Appointment appointment) {
        AppointmentEntity entity = appointmentMapper.toEntity(appointment);
        if (appointment.getPatientId() != null) {
            entity.setPatient(patientJpaRepository.getReferenceById(appointment.getPatientId()));
        }
        if (appointment.getDoctorId() != null) {
            entity.setDoctor(doctorJpaRepository.getReferenceById(appointment.getDoctorId()));
        }
        if (appointment.getServiceId() != null) {
            entity.setService(serviceJpaRepository.getReferenceById(appointment.getServiceId()));
        }
        if (appointment.getSedeId() != null) {
            entity.setSede(sedeJpaRepository.getReferenceById(appointment.getSedeId()));
        }
        entity = appointmentJpaRepository.save(entity);
        return appointmentMapper.toDomain(entity);
    }

    @Override
    public Optional<Appointment> findById(UUID id) {
        return appointmentJpaRepository.findById(id).map(appointmentMapper::toDomain);
    }

    @Override
    public List<Appointment> findByDoctorAndDate(UUID doctorId, LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        return appointmentJpaRepository.findByDoctorIdAndDate(doctorId, dayStart, dayEnd).stream()
                .map(appointmentMapper::toDomain)
                .toList();
    }

    @Override
    public List<Appointment> findByPatientId(UUID patientId, Pageable pageable) {
        return appointmentJpaRepository.findByPatientIdOrderByStartTimeDesc(patientId, pageable).stream()
                .map(appointmentMapper::toDomain)
                .toList();
    }

    @Override
    public List<Appointment> findAll(Pageable pageable) {
        return appointmentJpaRepository.findAllByOrderByStartTimeDesc(pageable).stream()
                .map(appointmentMapper::toDomain)
                .toList();
    }

    @Override
    public boolean hasConflict(UUID doctorId, LocalDateTime start, LocalDateTime end, UUID excludeId) {
        return appointmentJpaRepository.existsByDoctorAndTimeOverlap(
                doctorId,
                start,
                end,
                excludeId,
                com.clinica.salud.modules.agenda.domain.model.AppointmentStatus.CANCELLED,
                com.clinica.salud.modules.agenda.domain.model.AppointmentStatus.NO_SHOW
        );
    }

    @Override
    public List<Appointment> findBySedeAndDateRange(UUID sedeId, LocalDateTime start, LocalDateTime end) {
        return appointmentJpaRepository.findBySedeIdAndTimeOverlap(sedeId, start, end).stream()
                .map(appointmentMapper::toDomain)
                .toList();
    }
}
