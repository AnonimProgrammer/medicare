package com.ironhack.application.dto;

import java.time.OffsetDateTime;

import com.ironhack.domain.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PatientAppointmentDTO extends BaseDto {
    private OffsetDateTime appointmentTime;
    private AppointmentStatus status;
    private DoctorDTO doctor;
}
