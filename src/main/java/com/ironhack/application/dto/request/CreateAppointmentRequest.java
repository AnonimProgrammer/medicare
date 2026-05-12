package com.ironhack.application.dto.request;

import com.ironhack.domain.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAppointmentRequest {

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;

    @NotNull(message = "Appointment time is required")
    @FutureOrPresent(message = "Appointment time must be in the present or future")
    private LocalDateTime appointmentTime;

    @NotNull(message = "Appointment status is required")
    private AppointmentStatus status;
}

