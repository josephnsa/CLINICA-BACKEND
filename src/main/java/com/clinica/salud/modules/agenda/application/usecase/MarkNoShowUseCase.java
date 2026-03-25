package com.clinica.salud.modules.agenda.application.usecase;

import com.clinica.salud.modules.agenda.application.dto.AppointmentResponse;
import com.clinica.salud.modules.agenda.domain.model.Appointment;
import com.clinica.salud.modules.agenda.domain.port.AppointmentRepository;
import com.clinica.salud.modules.agenda.infrastructure.persistence.AppointmentMapper;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarkNoShowUseCase {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public AppointmentResponse execute(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", appointmentId.toString()));
        appointment.markNoShow();
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }
}
