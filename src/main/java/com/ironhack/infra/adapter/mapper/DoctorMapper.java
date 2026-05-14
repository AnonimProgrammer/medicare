package com.ironhack.infra.adapter.mapper;

import java.util.List;

import com.ironhack.application.dto.DoctorDTO;
import com.ironhack.application.dto.request.CreateDoctorRequest;
import com.ironhack.domain.DoctorEntity;
import com.ironhack.infra.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface DoctorMapper {
    DoctorDTO toDoctorDTO(DoctorEntity doctorEntity);

    List<DoctorDTO> toDoctorDTOList(List<DoctorEntity> doctorEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DoctorEntity toDoctorEntity(CreateDoctorRequest createDoctorRequest);
}
