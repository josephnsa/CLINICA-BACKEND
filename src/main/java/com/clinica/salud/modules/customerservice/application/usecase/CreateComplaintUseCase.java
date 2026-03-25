package com.clinica.salud.modules.customerservice.application.usecase;

import com.clinica.salud.modules.customerservice.application.dto.ComplaintResponse;
import com.clinica.salud.modules.customerservice.application.dto.CreateComplaintRequest;
import com.clinica.salud.modules.customerservice.domain.model.Complaint;
import com.clinica.salud.modules.customerservice.domain.model.ComplaintStatus;
import com.clinica.salud.modules.customerservice.domain.port.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateComplaintUseCase {

    private final ComplaintRepository complaintRepository;

    @Transactional
    public ComplaintResponse execute(CreateComplaintRequest request) {
        Complaint complaint = Complaint.builder()
                .patientId(request.getPatientId())
                .sedeId(request.getSedeId())
                .type(request.getType())
                .subject(request.getSubject())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(ComplaintStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();

        complaint.validate();
        Complaint saved = complaintRepository.save(complaint);
        return toResponse(saved);
    }

    private ComplaintResponse toResponse(Complaint c) {
        return ComplaintResponse.builder()
                .id(c.getId())
                .patientId(c.getPatientId())
                .sedeId(c.getSedeId())
                .type(c.getType())
                .subject(c.getSubject())
                .description(c.getDescription())
                .status(c.getStatus())
                .priority(c.getPriority())
                .isUrgent(c.isUrgent())
                .assignedTo(c.getAssignedTo())
                .resolvedAt(c.getResolvedAt())
                .resolution(c.getResolution())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
