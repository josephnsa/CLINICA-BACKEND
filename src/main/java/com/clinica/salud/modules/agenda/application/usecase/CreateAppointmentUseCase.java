package com.clinica.salud.modules.agenda.application.usecase;

import com.clinica.salud.modules.agenda.application.dto.AppointmentResponse;
import com.clinica.salud.modules.agenda.application.dto.CreateAppointmentRequest;
import com.clinica.salud.modules.agenda.domain.model.Appointment;
import com.clinica.salud.modules.agenda.domain.model.AppointmentStatus;
import com.clinica.salud.modules.agenda.domain.port.AppointmentRepository;
import com.clinica.salud.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;

    public AppointmentResponse execute(CreateAppointmentRequest req, UUID createdBy) {
        if (appointmentRepository.hasConflict(req.doctorId(), req.startTime(), req.endTime(), null)) {
            throw new BusinessRuleException("Ya existe una cita en ese horario para el médico");
        }
        Appointment appointment = Appointment.builder()
                .patientId(req.patientId())
                .doctorId(req.doctorId())
                .serviceId(req.serviceId())
                .sedeId(req.sedeId())
                .startTime(req.startTime())
                .endTime(req.endTime())
                .status(AppointmentStatus.PENDING)
                .notes(null)
                .cancellationReason(null)
                .createdBy(createdBy)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
        Appointment saved = appointmentRepository.save(appointment);
        return toResponse(saved);
    }

    private static AppointmentResponse toResponse(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getPatientId(),
                a.getDoctorId(),
                a.getServiceId(),
                a.getSedeId(),
                a.getStartTime(),
                a.getEndTime(),
                a.getStatus(),
                a.getNotes(),
                a.getCancellationReason(),
                a.getCreatedBy(),
                a.getCreatedAt(),
                a.getUpdatedAt(),
                null,
                null,
                null
        );
    }
}
