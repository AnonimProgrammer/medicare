package com.ironhack.application.usecase;

import org.springframework.stereotype.Service;

import com.ironhack.application.dto.DoctorDTO;
import com.ironhack.application.dto.request.CreateDoctorRequest;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.exception.ConflictException;
import com.ironhack.domain.DoctorEntity;
import com.ironhack.infra.adapter.mapper.MappingFacade;
import com.ironhack.infra.adapter.output.DoctorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateDoctorUseCase {
    private final DoctorRepository doctorRepository;
    private final MappingFacade mappingFacade;

    public ApiResponse<DoctorDTO> invoke(CreateDoctorRequest request) {
        rejectDuplicate(request.fullName());

        DoctorEntity entity = mappingFacade.toDoctorEntity(request);
        DoctorEntity saved = doctorRepository.save(entity);
        DoctorDTO dto = mappingFacade.toDoctorDTO(saved);

        return ApiResponse.created(dto, "Doctor created successfully.");
    }

    private void rejectDuplicate(String fullName) {
        if (doctorRepository.existsByFullName(fullName)) {
            throw new ConflictException("A doctor with the name '" + fullName + "' already exists.");
        }
    }
}
