package com.ironhack.infra.adapter.mapper;

import com.ironhack.application.dto.DoctorDTO;
import com.ironhack.application.dto.request.CreateDoctorRequest;
import com.ironhack.domain.DoctorEntity;
import com.ironhack.infra.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface DoctorMapper {
    DoctorDTO toDoctorDTO(DoctorEntity doctorEntity);

    @Mapping(target = "id", ignore = true)
    DoctorEntity toDoctorEntity(CreateDoctorRequest createDoctorRequest);
}
