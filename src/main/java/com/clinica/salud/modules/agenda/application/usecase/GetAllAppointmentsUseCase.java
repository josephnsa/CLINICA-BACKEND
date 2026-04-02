package com.clinica.salud.modules.agenda.application.usecase;

import com.clinica.salud.modules.agenda.domain.model.Appointment;
import com.clinica.salud.modules.agenda.domain.port.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllAppointmentsUseCase {

    private final AppointmentRepository appointmentRepository;

    public List<Appointment> execute(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }
}
