package com.ironhack.infra.adapter.mapper;

import com.ironhack.application.dto.PatientDTO;
import com.ironhack.application.dto.request.CreatePatientRequest;
import com.ironhack.domain.PatientEntity;
import com.ironhack.infra.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface PatientMapper {
    PatientDTO toPatientDTO(PatientEntity patientEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    PatientEntity toPatientEntity(CreatePatientRequest createPatientRequest);
}
