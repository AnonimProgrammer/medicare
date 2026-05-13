package com.ironhack.application.usecase;

import org.springframework.stereotype.Service;

import com.ironhack.application.dto.PatientDTO;
import com.ironhack.application.dto.request.CreatePatientRequest;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.exception.ConflictException;
import com.ironhack.domain.PatientEntity;
import com.ironhack.infra.adapter.mapper.MappingFacade;
import com.ironhack.infra.adapter.output.PatientRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreatePatientUseCase {
    private final PatientRepository patientRepository;
    private final MappingFacade mappingFacade;

    public ApiResponse<PatientDTO> invoke(CreatePatientRequest request) {
        rejectDuplicate(request.phoneNumber());

        PatientEntity entity = mappingFacade.toPatientEntity(request);
        PatientEntity saved = patientRepository.save(entity);
        PatientDTO dto = mappingFacade.toPatientDTO(saved);

        return ApiResponse.created(dto, "Patient created successfully.");
    }

    private void rejectDuplicate(String phone) {
        if (patientRepository.existsByPhoneNumber(phone)) {
            throw new ConflictException("A patient with phone number '" + phone + "' already exists.");
        }
    }
}
