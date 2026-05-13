package com.ironhack.infra.adapter.mapper;

import com.ironhack.application.dto.AppointmentDTO;
import com.ironhack.domain.AppointmentEntity;
import com.ironhack.infra.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(
        config = MapStructConfig.class,
        uses = {DoctorMapper.class, PatientMapper.class})
public interface AppointmentMapper {
    AppointmentDTO toAppointmentDTO(AppointmentEntity appointmentEntity);
}
