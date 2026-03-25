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
public class ResolveComplaintUseCase {

    private final ComplaintRepository complaintRepository;

    @Transactional
    public ComplaintResponse execute(UUID complaintId, String resolution) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamo", complaintId.toString()));

        complaint.resolve(resolution);
        Complaint saved = complaintRepository.save(complaint);

        return ComplaintResponse.builder()
                .id(saved.getId())
                .patientId(saved.getPatientId())
                .sedeId(saved.getSedeId())
                .type(saved.getType())
                .subject(saved.getSubject())
                .description(saved.getDescription())
                .status(saved.getStatus())
                .priority(saved.getPriority())
                .isUrgent(saved.isUrgent())
                .assignedTo(saved.getAssignedTo())
                .resolvedAt(saved.getResolvedAt())
                .resolution(saved.getResolution())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
