package com.ironhack.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ironhack.domain.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {
    private UUID id;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private DoctorDTO doctor;
    private PatientDTO patient;
}
