package com.ironhack.infra.adapter.mapper;

import com.ironhack.application.dto.DoctorDTO;
import com.ironhack.application.dto.request.RegisterDoctorRequest;
import com.ironhack.domain.DoctorEntity;
import com.ironhack.infra.adapter.mapper.config.MapStructMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructMapperConfig.class)
public interface DoctorMapper {

    DoctorDTO toDoctorDTO(DoctorEntity doctorEntity);

    @Mapping(target = "appointments", ignore = true)
    DoctorEntity toDoctorEntity(DoctorDTO doctorDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    DoctorEntity toDoctoEntity(RegisterDoctorRequest registerDoctorRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    void updateDoctorEntityFromRequest(RegisterDoctorRequest request, @MappingTarget DoctorEntity doctorEntity);
}



