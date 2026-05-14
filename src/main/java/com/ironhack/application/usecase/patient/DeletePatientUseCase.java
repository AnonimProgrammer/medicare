package com.ironhack.application.usecase.patient;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.exception.NotFoundException;
import com.ironhack.infra.adapter.output.AppointmentRepository;
import com.ironhack.infra.adapter.output.PatientRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeletePatientUseCase {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public ApiResponse<Void> invoke(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new NotFoundException("Patient not found.");
        }
        appointmentRepository.deleteByPatient_Id(patientId);
        patientRepository.deleteById(patientId);
        return ApiResponse.success(null, "Patient deleted successfully.");
    }
}
