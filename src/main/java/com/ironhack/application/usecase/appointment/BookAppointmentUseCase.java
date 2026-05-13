package com.ironhack.application.usecase.appointment;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public ApiResponse<AppointmentDTO> invoke(BookAppointmentRequest request) {
        ensureAppointmentTimeInFuture(request.appointmentTime());
        PatientEntity patient = requirePatient(request.patientId());
        DoctorEntity doctor = requireDoctorWithSpecialty(request.doctorId());
        ensureDoctorHasNoSchedulingConflict(request.doctorId(), request.appointmentTime());

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

    private void ensureAppointmentTimeInFuture(LocalDateTime appointmentTime) {
        if (appointmentTime.isBefore(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("Appointment time must not be in the past.");
        }
    }

    private PatientEntity requirePatient(UUID patientId) {
        return patientRepository.findById(patientId).orElseThrow(() -> new NotFoundException("Patient not found."));
    }

    private DoctorEntity requireDoctorWithSpecialty(UUID doctorId) {
        DoctorEntity doctor =
                doctorRepository.findById(doctorId).orElseThrow(() -> new NotFoundException("Doctor not found."));
        if (doctor.getSpecialty() == null) {
            throw new ConflictException("Doctor has no specialty assigned; cannot book appointments.");
        }
        return doctor;
    }

    private void ensureDoctorHasNoSchedulingConflict(UUID doctorId, LocalDateTime appointmentTime) {
        if (appointmentRepository.existsByDoctor_IdAndAppointmentTimeAndStatus(
                doctorId, appointmentTime, AppointmentStatus.SCHEDULED)) {
            throw new ConflictException("The doctor already has a scheduled appointment at this time.");
        }
    }
}
