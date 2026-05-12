package com.ironhack.infra.adapter.mapper;

import java.util.UUID;

import com.ironhack.application.dto.AppointmentDTO;
import com.ironhack.application.dto.request.CreateAppointmentRequest;
import com.ironhack.domain.AppointmentEntity;
import com.ironhack.domain.DoctorEntity;
import com.ironhack.domain.PatientEntity;
import com.ironhack.infra.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        config = MapStructConfig.class,
        uses = {DoctorMapper.class, PatientMapper.class})
public interface AppointmentMapper {

    AppointmentDTO toAppointmentDTO(AppointmentEntity appointmentEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "patientId", target = "patient", qualifiedByName = "patientRefFromId")
    @Mapping(source = "doctorId", target = "doctor", qualifiedByName = "doctorRefFromId")
    AppointmentEntity toAppointmentEntity(CreateAppointmentRequest request);

    @Named("patientRefFromId")
    default PatientEntity patientRefFromId(UUID patientId) {
        if (patientId == null) {
            return null;
        }
        return PatientEntity.builder().id(patientId).build();
    }

    @Named("doctorRefFromId")
    default DoctorEntity doctorRefFromId(UUID doctorId) {
        if (doctorId == null) {
            return null;
        }
        return DoctorEntity.builder().id(doctorId).build();
    }
}
