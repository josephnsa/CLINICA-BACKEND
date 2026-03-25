package com.clinica.salud.modules.exam.application.usecase;

import com.clinica.salud.modules.exam.application.dto.ExamOrderItemResponse;
import com.clinica.salud.modules.exam.application.dto.ExamOrderResponse;
import com.clinica.salud.modules.exam.domain.port.ExamOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetExamOrdersByPatientUseCase {

    private final ExamOrderRepository examOrderRepository;

    public List<ExamOrderResponse> execute(UUID patientId) {
        return examOrderRepository.findByPatient(patientId).stream()
                .map(order -> new ExamOrderResponse(
                        order.getId(),
                        order.getPatientId(),
                        order.getDoctorId(),
                        order.getAppointmentId(),
                        order.getStatus(),
                        order.getNotes(),
                        order.getCreatedAt(),
                        order.getCreatedBy(),
                        order.getItems().stream()
                                .map(i -> new ExamOrderItemResponse(
                                        i.getId(),
                                        i.getServiceId(),
                                        i.getStatus(),
                                        i.getResultText(),
                                        i.getResultAt(),
                                        i.getResultBy()
                                ))
                                .toList()
                ))
                .toList();
    }
}

