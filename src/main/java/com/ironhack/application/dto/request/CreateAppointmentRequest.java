package com.ironhack.application.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ironhack.domain.AppointmentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public record CreateAppointmentRequest(
        @NotNull(message = "Patient ID is required") UUID patientId,
        @NotNull(message = "Doctor ID is required") UUID doctorId,

        @NotNull(message = "Appointment time is required")
        @FutureOrPresent(message = "Appointment time must be in the present or future")
        LocalDateTime appointmentTime,

        @NotNull(message = "Appointment status is required") AppointmentStatus status) {}
