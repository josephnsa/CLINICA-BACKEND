package com.clinica.salud.modules.patientportal.application.usecase;

import com.clinica.salud.modules.patientportal.application.dto.PortalPrescriptionResponse;
import com.clinica.salud.modules.patientportal.domain.port.PortalPrescriptionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetMyPrescriptionsUseCase {

    private final PortalPrescriptionPort prescriptionPort;

    @Transactional(readOnly = true)
    public List<PortalPrescriptionResponse> execute(UUID patientId) {
        return prescriptionPort.findByPatientId(patientId);
    }
}
