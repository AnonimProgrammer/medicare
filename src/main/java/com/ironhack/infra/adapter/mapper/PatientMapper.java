package com.ironhack.infra.adapter.mapper;

import com.ironhack.application.dto.PatientDTO;
import com.ironhack.application.dto.request.CreatePatientRequest;
import com.ironhack.domain.PatientEntity;
import com.ironhack.infra.adapter.mapper.config.MapStructMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructMapperConfig.class, uses = AppointmentMapper.class)
public interface PatientMapper {

    PatientDTO toPatientDTO(PatientEntity patientEntity);

    PatientEntity toPatientEntity(PatientDTO patientDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    PatientEntity toPatientEntity(CreatePatientRequest createPatientRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    void updatePatientEntityFromRequest(CreatePatientRequest request, @MappingTarget PatientEntity patientEntity);
}


