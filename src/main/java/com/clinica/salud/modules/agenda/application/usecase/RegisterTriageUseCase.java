package com.clinica.salud.modules.agenda.application.usecase;

import com.clinica.salud.modules.agenda.application.dto.RegisterTriageRequest;
import com.clinica.salud.modules.agenda.application.dto.TriageResponse;
import com.clinica.salud.modules.agenda.domain.model.Triage;
import com.clinica.salud.modules.agenda.domain.model.TriageLevel;
import com.clinica.salud.modules.agenda.domain.port.TriageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterTriageUseCase {

    private final TriageRepository triageRepository;

    @Transactional
    public TriageResponse execute(RegisterTriageRequest request, UUID recordedBy) {
        Triage triage = Triage.builder()
                .appointmentId(request.appointmentId())
                .patientId(request.patientId())
                .recordedBy(recordedBy)
                .bloodPressure(request.bloodPressure())
                .heartRate(request.heartRate())
                .respiratoryRate(request.respiratoryRate())
                .temperature(request.temperature())
                .oxygenSaturation(request.oxygenSaturation())
                .weight(request.weight())
                .height(request.height())
                .triageLevel(request.triageLevel() != null ? request.triageLevel() : TriageLevel.NORMAL)
                .notes(request.notes())
                .recordedAt(LocalDateTime.now())
                .build();
        triage.validate();
        return toResponse(triageRepository.save(triage));
    }

    public TriageResponse getByAppointment(UUID appointmentId) {
        return triageRepository.findByAppointmentId(appointmentId)
                .map(this::toResponse)
                .orElse(null);
    }

    public List<TriageResponse> getByPatient(UUID patientId) {
        return triageRepository.findByPatientId(patientId).stream()
                .map(this::toResponse)
                .toList();
    }

    private TriageResponse toResponse(Triage t) {
        return new TriageResponse(
                t.getId(),
                t.getAppointmentId(),
                t.getPatientId(),
                t.getRecordedBy(),
                t.getBloodPressure(),
                t.getHeartRate(),
                t.getRespiratoryRate(),
                t.getTemperature(),
                t.getOxygenSaturation(),
                t.getWeight(),
                t.getHeight(),
                t.getTriageLevel(),
                t.getNotes(),
                t.getRecordedAt(),
                t.isCritical(),
                t.getBmi()
        );
    }
}
