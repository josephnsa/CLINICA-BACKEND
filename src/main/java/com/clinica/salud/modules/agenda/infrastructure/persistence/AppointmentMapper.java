package com.clinica.salud.modules.agenda.infrastructure.persistence;

import com.clinica.salud.modules.agenda.application.dto.AppointmentResponse;
import com.clinica.salud.modules.agenda.domain.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "patientId", expression = "java(entity.getPatient() != null ? entity.getPatient().getId() : null)")
    @Mapping(target = "doctorId", expression = "java(entity.getDoctor() != null ? entity.getDoctor().getId() : null)")
    @Mapping(target = "serviceId", expression = "java(entity.getService() != null ? entity.getService().getId() : null)")
    @Mapping(target = "sedeId", expression = "java(entity.getSede() != null ? entity.getSede().getId() : null)")
    Appointment toDomain(AppointmentEntity entity);

    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "sede", ignore = true)
    AppointmentEntity toEntity(Appointment domain);

    @Mapping(target = "patientFullName", expression = "java((String) null)")
    @Mapping(target = "doctorFullName", expression = "java((String) null)")
    @Mapping(target = "serviceName", expression = "java((String) null)")
    AppointmentResponse toResponse(Appointment domain);
}
