package com.ironhack.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ironhack.application.dto.DoctorDTO;
import com.ironhack.application.dto.request.AssignDoctorSpecialtyRequest;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.exception.NotFoundException;
import com.ironhack.domain.DoctorEntity;
import com.ironhack.infra.adapter.mapper.MappingFacade;
import com.ironhack.infra.adapter.output.DoctorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssignSpecialtyToDoctorUseCase {
    private final DoctorRepository doctorRepository;
    private final MappingFacade mappingFacade;

    public ApiResponse<DoctorDTO> invoke(UUID doctorId, AssignDoctorSpecialtyRequest request) {
        DoctorEntity doctor =
                doctorRepository.findById(doctorId).orElseThrow(() -> new NotFoundException("Doctor not found"));

        doctor.setSpecialty(request.specialty());
        DoctorEntity saved = doctorRepository.save(doctor);
        DoctorDTO dto = mappingFacade.toDoctorDTO(saved);

        return ApiResponse.success(dto, "Doctor specialty assigned successfully.");
    }
}
