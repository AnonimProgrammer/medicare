package com.ironhack.infra.adapter.mapper;

import java.util.List;

import com.ironhack.application.dto.AppointmentDTO;
import com.ironhack.application.dto.DoctorAppointmentDTO;
import com.ironhack.application.dto.PatientAppointmentDTO;
import com.ironhack.domain.AppointmentEntity;
import com.ironhack.infra.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(
        config = MapStructConfig.class,
        uses = {DoctorMapper.class, PatientMapper.class})
public interface AppointmentMapper {
    AppointmentDTO toAppointmentDTO(AppointmentEntity appointmentEntity);

    PatientAppointmentDTO toPatientAppointmentDTO(AppointmentEntity appointmentEntity);

    List<PatientAppointmentDTO> toPatientAppointmentDTOList(List<AppointmentEntity> appointmentEntities);

    DoctorAppointmentDTO toDoctorAppointmentDTO(AppointmentEntity appointmentEntity);

    List<DoctorAppointmentDTO> toDoctorAppointmentDTOList(List<AppointmentEntity> appointmentEntities);
}
