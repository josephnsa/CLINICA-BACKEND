package com.clinica.salud.modules.agenda.application.usecase;

import com.clinica.salud.modules.agenda.domain.model.Appointment;
import com.clinica.salud.modules.agenda.domain.port.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetAppointmentsByPatientUseCase {

    private final AppointmentRepository appointmentRepository;

    public List<Appointment> execute(UUID patientId, Pageable pageable) {
        return appointmentRepository.findByPatientId(patientId, pageable);
    }
}
