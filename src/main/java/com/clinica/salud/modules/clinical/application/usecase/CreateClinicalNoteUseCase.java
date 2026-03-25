package com.clinica.salud.modules.clinical.application.usecase;

import com.clinica.salud.modules.agenda.domain.model.Appointment;
import com.clinica.salud.modules.agenda.domain.model.AppointmentStatus;
import com.clinica.salud.modules.agenda.domain.port.AppointmentRepository;
import com.clinica.salud.modules.clinical.application.dto.ClinicalNoteResponse;
import com.clinica.salud.modules.clinical.application.dto.CreateClinicalNoteRequest;
import com.clinica.salud.modules.clinical.domain.model.ClinicalNote;
import com.clinica.salud.modules.clinical.domain.port.Cie10ExistsPort;
import com.clinica.salud.modules.clinical.domain.port.ClinicalNoteRepository;
import com.clinica.salud.modules.clinical.domain.port.GetDoctorIdByUserIdPort;
import com.clinica.salud.shared.exception.BusinessRuleException;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateClinicalNoteUseCase {

    private final ClinicalNoteRepository clinicalNoteRepository;
    private final AppointmentRepository appointmentRepository;
    private final GetDoctorIdByUserIdPort getDoctorIdByUserIdPort;
    private final Cie10ExistsPort cie10ExistsPort;

    public ClinicalNoteResponse execute(CreateClinicalNoteRequest request, UUID currentUserId) {
        UUID doctorId = getDoctorIdByUserIdPort.getDoctorIdByUserId(currentUserId)
                .orElseThrow(() -> new BusinessRuleException("El usuario no está asociado a un médico"));

        Appointment appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", request.appointmentId().toString()));

        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new BusinessRuleException("La cita no pertenece al médico autenticado");
        }

        if (!cie10ExistsPort.existsByCode(request.diagnosisCode())) {
            throw new BusinessRuleException("Código CIE-10 no válido: " + request.diagnosisCode());
        }

        LocalDateTime now = LocalDateTime.now();
        ClinicalNote note = ClinicalNote.builder()
                .appointmentId(request.appointmentId())
                .patientId(appointment.getPatientId())
                .doctorId(doctorId)
                .reason(request.reason())
                .physicalExam(request.physicalExam())
                .diagnosisCode(request.diagnosisCode())
                .diagnosisDesc(request.diagnosisDescription())
                .treatmentPlan(request.treatmentPlan())
                .createdAt(now)
                .build();
        note = clinicalNoteRepository.save(note);

        appointment.setStatus(AppointmentStatus.ATTENDED);
        appointment.setUpdatedAt(now);
        appointmentRepository.save(appointment);

        return toResponse(note);
    }

    private static ClinicalNoteResponse toResponse(ClinicalNote n) {
        return new ClinicalNoteResponse(
                n.getId(),
                n.getAppointmentId(),
                n.getPatientId(),
                n.getDoctorId(),
                n.getReason(),
                n.getPhysicalExam(),
                n.getDiagnosisCode(),
                n.getDiagnosisDesc(),
                n.getTreatmentPlan(),
                n.getCreatedAt(),
                null,
                null
        );
    }
}
