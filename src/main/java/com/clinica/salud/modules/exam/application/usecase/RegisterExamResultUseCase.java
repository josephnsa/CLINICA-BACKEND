package com.clinica.salud.modules.exam.application.usecase;

import com.clinica.salud.modules.exam.application.dto.ExamOrderItemResponse;
import com.clinica.salud.modules.exam.application.dto.ExamOrderResponse;
import com.clinica.salud.modules.exam.application.dto.RegisterExamResultRequest;
import com.clinica.salud.modules.exam.domain.model.ExamOrder;
import com.clinica.salud.modules.exam.domain.model.ExamOrderItem;
import com.clinica.salud.modules.exam.domain.model.ExamOrderStatus;
import com.clinica.salud.modules.exam.domain.port.ExamOrderRepository;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterExamResultUseCase {

    private final ExamOrderRepository examOrderRepository;

    public ExamOrderResponse execute(UUID orderId, RegisterExamResultRequest request, UUID userId) {
        ExamOrder order = examOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamOrder", orderId.toString()));

        OffsetDateTime now = OffsetDateTime.now();

        List<ExamOrderItem> updatedItems = order.getItems().stream()
                .map(i -> {
                    i.setResultText(request.resultText());
                    i.setResultAt(now);
                    i.setResultBy(userId);
                    i.setStatus(ExamOrderStatus.LISTO);
                    return i;
                })
                .toList();

        order.setItems(updatedItems);
        order.setStatus(ExamOrderStatus.LISTO);

        ExamOrder saved = examOrderRepository.save(order);

        List<ExamOrderItemResponse> itemResponses = saved.getItems().stream()
                .map(i -> new ExamOrderItemResponse(
                        i.getId(),
                        i.getServiceId(),
                        i.getStatus(),
                        i.getResultText(),
                        i.getResultAt(),
                        i.getResultBy()
                ))
                .toList();

        return new ExamOrderResponse(
                saved.getId(),
                saved.getPatientId(),
                saved.getDoctorId(),
                saved.getAppointmentId(),
                saved.getStatus(),
                saved.getNotes(),
                saved.getCreatedAt(),
                saved.getCreatedBy(),
                itemResponses
        );
    }
}

