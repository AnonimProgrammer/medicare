package com.ironhack.application.usecase.patient;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ironhack.application.dto.PatientDTO;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.infra.adapter.mapper.MappingFacade;
import com.ironhack.infra.adapter.output.PatientRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListPatientsUseCase {
    private final PatientRepository patientRepository;
    private final MappingFacade mappingFacade;

    @Transactional(readOnly = true)
    public ApiResponse<List<PatientDTO>> invoke() {
        var entities = patientRepository.findAll();
        var dtos = mappingFacade.toPatientDTOList(entities);

        return ApiResponse.success(dtos, "Patients retrieved successfully.");
    }
}
