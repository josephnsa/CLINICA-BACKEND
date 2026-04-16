package com.clinica.salud.modules.patientportal.application.usecase;

import com.clinica.salud.modules.patientportal.application.dto.PortalAppointmentResponse;
import com.clinica.salud.modules.patientportal.domain.port.PortalAppointmentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetMyAppointmentsUseCase {

    private final PortalAppointmentPort appointmentPort;

    @Transactional(readOnly = true)
    public List<PortalAppointmentResponse> execute(UUID patientId) {
        return appointmentPort.findByPatientId(patientId);
    }
}
