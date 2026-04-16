package com.clinica.salud.modules.customerservice.application.usecase;

import com.clinica.salud.modules.customerservice.application.dto.ComplaintResponse;
import com.clinica.salud.modules.customerservice.domain.model.Complaint;
import com.clinica.salud.modules.customerservice.domain.model.ComplaintStatus;
import com.clinica.salud.modules.customerservice.domain.port.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListComplaintsUseCase {

    private final ComplaintRepository complaintRepository;

    @Transactional(readOnly = true)
    public List<ComplaintResponse> execute(UUID patientId, UUID sedeId, ComplaintStatus status) {
        List<Complaint> complaints;

        if (patientId != null) {
            complaints = complaintRepository.findByPatientId(patientId);
        } else if (sedeId != null) {
            complaints = complaintRepository.findBySedeId(sedeId);
        } else if (status != null) {
            complaints = complaintRepository.findByStatus(status);
        } else {
            complaints = complaintRepository.findAll();
        }

        return complaints.stream().map(this::toResponse).collect(Collectors.toList());
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
