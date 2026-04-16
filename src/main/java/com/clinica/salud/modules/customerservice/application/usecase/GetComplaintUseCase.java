package com.clinica.salud.modules.customerservice.application.usecase;

import com.clinica.salud.modules.customerservice.application.dto.ComplaintResponse;
import com.clinica.salud.modules.customerservice.domain.model.Complaint;
import com.clinica.salud.modules.customerservice.domain.port.ComplaintRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetComplaintUseCase {

    private final ComplaintRepository complaintRepository;

    @Transactional(readOnly = true)
    public ComplaintResponse execute(UUID id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", id.toString()));
        return toResponse(complaint);
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
