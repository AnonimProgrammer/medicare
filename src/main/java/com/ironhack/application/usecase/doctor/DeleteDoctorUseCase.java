package com.ironhack.application.usecase.doctor;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.exception.NotFoundException;
import com.ironhack.infra.adapter.output.AppointmentRepository;
import com.ironhack.infra.adapter.output.DoctorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteDoctorUseCase {
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public ApiResponse<Void> invoke(UUID doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new NotFoundException("Doctor not found.");
        }
        appointmentRepository.deleteByDoctor_Id(doctorId);
        doctorRepository.deleteById(doctorId);
        return ApiResponse.success(null, "Doctor deleted successfully.");
    }
}
