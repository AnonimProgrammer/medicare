package com.ironhack.application.usecase;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.ironhack.application.dto.AppointmentDTO;
import com.ironhack.application.dto.request.BookAppointmentRequest;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.exception.ConflictException;
import com.ironhack.application.exception.NotFoundException;
import com.ironhack.domain.AppointmentEntity;
import com.ironhack.domain.AppointmentStatus;
import com.ironhack.domain.DoctorEntity;
import com.ironhack.domain.PatientEntity;
import com.ironhack.infra.adapter.mapper.MappingFacade;
import com.ironhack.infra.adapter.output.AppointmentRepository;
import com.ironhack.infra.adapter.output.DoctorRepository;
import com.ironhack.infra.adapter.output.PatientRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookAppointmentUseCase {
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final MappingFacade mappingFacade;
    private final Clock clock;

    public ApiResponse<AppointmentDTO> invoke(BookAppointmentRequest request) {
        LocalDateTime now = LocalDateTime.now(clock);
        if (request.appointmentTime().isBefore(now)) {
            throw new IllegalArgumentException("Appointment time must not be in the past.");
        }

        PatientEntity patient = patientRepository
                .findById(request.patientId())
                .orElseThrow(() -> new NotFoundException("Patient not found."));

        DoctorEntity doctor = doctorRepository
                .findById(request.doctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found."));

        if (doctor.getSpecialty() == null) {
            throw new ConflictException("Doctor has no specialty assigned; cannot book appointments.");
        }

        if (appointmentRepository.existsByDoctor_IdAndAppointmentTimeAndStatus(
                request.doctorId(), request.appointmentTime(), AppointmentStatus.SCHEDULED)) {
            throw new ConflictException("The doctor already has a scheduled appointment at this time.");
        }

        AppointmentEntity entity = AppointmentEntity.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentTime(request.appointmentTime())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        AppointmentEntity saved = appointmentRepository.save(entity);
        AppointmentDTO dto = mappingFacade.toAppointmentDTO(saved);

        return ApiResponse.created(dto, "Appointment booked successfully.");
    }
}
