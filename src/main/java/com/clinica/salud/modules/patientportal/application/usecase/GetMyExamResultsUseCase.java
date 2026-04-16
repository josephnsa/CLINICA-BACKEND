package com.clinica.salud.modules.patientportal.application.usecase;

import com.clinica.salud.modules.patientportal.application.dto.PortalExamResponse;
import com.clinica.salud.modules.patientportal.domain.port.PortalExamPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetMyExamResultsUseCase {

    private final PortalExamPort examPort;

    @Transactional(readOnly = true)
    public List<PortalExamResponse> execute(UUID patientId) {
        return examPort.findByPatientId(patientId);
    }
}
