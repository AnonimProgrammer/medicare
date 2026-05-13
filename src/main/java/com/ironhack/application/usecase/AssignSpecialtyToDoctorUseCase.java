package com.ironhack.application.usecase;

import com.ironhack.application.dto.DoctorDTO;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.exception.NotFoundException;
import com.ironhack.domain.DoctorEntity;
import com.ironhack.domain.Specialty;
import com.ironhack.infra.adapter.mapper.MappingFacade;
import com.ironhack.infra.adapter.output.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignSpecialtyToDoctorUseCase {

    private final DoctorRepository doctorRepository;
    private final MappingFacade mappingFacade;

    public ApiResponse<DoctorDTO> invoke(UUID doctorId, Specialty specialty) {

        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new NotFoundException("Doctor not found with id: " + doctorId)
                );

        doctor.setSpecialty(specialty);

        DoctorEntity saved = doctorRepository.save(doctor);

        DoctorDTO dto = mappingFacade.toDoctorDTO(saved);

        return ApiResponse.success(dto, "Doctor specialty assigned successfully.");
    }
}