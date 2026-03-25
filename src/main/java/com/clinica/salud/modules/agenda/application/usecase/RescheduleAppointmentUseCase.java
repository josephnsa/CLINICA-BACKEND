package com.clinica.salud.modules.agenda.application.usecase;

import com.clinica.salud.modules.agenda.application.dto.AppointmentResponse;
import com.clinica.salud.modules.agenda.application.dto.RescheduleRequest;
import com.clinica.salud.modules.agenda.domain.model.Appointment;
import com.clinica.salud.modules.agenda.domain.port.AppointmentRepository;
import com.clinica.salud.modules.agenda.infrastructure.persistence.AppointmentMapper;
import com.clinica.salud.shared.exception.BusinessRuleException;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RescheduleAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public AppointmentResponse execute(UUID appointmentId, RescheduleRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", appointmentId.toString()));

        // Validar conflicto con el nuevo horario
        boolean conflict = appointmentRepository.hasConflict(
                appointment.getDoctorId(),
                request.getNewStart(),
                request.getNewEnd(),
                appointmentId);
        if (conflict) {
            throw new BusinessRuleException("El médico ya tiene una cita en el nuevo horario solicitado");
        }

        appointment.reschedule(request.getNewStart(), request.getNewEnd());
        if (request.getReason() != null && !request.getReason().isBlank()) {
            appointment.setNotes(request.getReason());
        }
        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(saved);
    }
}
