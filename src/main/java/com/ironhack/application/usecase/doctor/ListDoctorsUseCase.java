package com.ironhack.application.usecase.doctor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ironhack.application.dto.DoctorDTO;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.infra.adapter.mapper.MappingFacade;
import com.ironhack.infra.adapter.output.DoctorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListDoctorsUseCase {
    private final DoctorRepository doctorRepository;
    private final MappingFacade mappingFacade;

    @Transactional(readOnly = true)
    public ApiResponse<List<DoctorDTO>> invoke() {
        var entities = doctorRepository.findAll();
        var dtos = mappingFacade.toDoctorDTOList(entities);
        return ApiResponse.success(dtos, "Doctors retrieved successfully.");
    }
}
