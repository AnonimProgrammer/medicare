package com.ironhack.infra.adapter.mapper;

import com.ironhack.application.dto.AppointmentDTO;
import com.ironhack.application.dto.request.CreateAppointmentRequest;
import com.ironhack.domain.AppointmentEntity;
import com.ironhack.domain.DoctorEntity;
import com.ironhack.domain.PatientEntity;
import com.ironhack.infra.adapter.mapper.config.MapStructMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapStructMapperConfig.class, uses = {DoctorMapper.class})
public interface AppointmentMapper {

    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "doctor.id", target = "doctorId")
    AppointmentDTO toAppointmentDTO(AppointmentEntity appointmentEntity);

    @Mapping(source = "patientId", target = "patient", qualifiedByName = "patientIdToPatient")
    @Mapping(source = "doctorId", target = "doctor", qualifiedByName = "doctorIdToDoctor")
    AppointmentEntity toAppointmentEntity(AppointmentDTO appointmentDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "patientId", target = "patient", qualifiedByName = "patientIdToPatient")
    @Mapping(source = "doctorId", target = "doctor", qualifiedByName = "doctorIdToDoctor")
    AppointmentEntity toAppointmentEntity(CreateAppointmentRequest createAppointmentRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "patientId", target = "patient", qualifiedByName = "patientIdToPatient")
    @Mapping(source = "doctorId", target = "doctor", qualifiedByName = "doctorIdToDoctor")
    void updateAppointmentEntityFromRequest(CreateAppointmentRequest request, @MappingTarget AppointmentEntity appointmentEntity);

    @Named("patientIdToPatient")
    default PatientEntity patientIdToPatient(java.util.UUID patientId) {
        if (patientId == null) {
            return null;
        }
        return PatientEntity.builder().id(patientId).build();
    }

    @Named("doctorIdToDoctor")
    default DoctorEntity doctorIdToDoctor(java.util.UUID doctorId) {
        if (doctorId == null) {
            return null;
        }
        return DoctorEntity.builder().id(doctorId).build();
    }
}




