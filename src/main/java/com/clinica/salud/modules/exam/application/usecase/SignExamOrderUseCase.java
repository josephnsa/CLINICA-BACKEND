package com.clinica.salud.modules.exam.application.usecase;

import com.clinica.salud.modules.exam.application.dto.ExamOrderItemResponse;
import com.clinica.salud.modules.exam.application.dto.ExamOrderResponse;
import com.clinica.salud.modules.exam.domain.model.ExamOrder;
import com.clinica.salud.modules.exam.domain.port.ExamOrderRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignExamOrderUseCase {

    private final ExamOrderRepository examOrderRepository;

    @Transactional
    public ExamOrderResponse execute(UUID orderId, UUID professionalId) {
        ExamOrder order = examOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamOrder", orderId.toString()));
        order.sign(professionalId);
        ExamOrder saved = examOrderRepository.save(order);

        List<ExamOrderItemResponse> itemResponses = saved.getItems().stream()
                .map(i -> new ExamOrderItemResponse(
                        i.getId(), i.getServiceId(), i.getStatus(),
                        i.getResultText(), i.getResultAt(), i.getResultBy()))
                .toList();

        return new ExamOrderResponse(
                saved.getId(), saved.getPatientId(), saved.getDoctorId(),
                saved.getAppointmentId(), saved.getStatus(), saved.getNotes(),
                saved.getCreatedAt(), saved.getCreatedBy(), itemResponses);
    }
}
