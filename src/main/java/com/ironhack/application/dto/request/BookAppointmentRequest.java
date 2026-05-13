package com.ironhack.application.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record BookAppointmentRequest(
        @NotNull(message = "Patient ID is required") UUID patientId,
        @NotNull(message = "Doctor ID is required") UUID doctorId,
        @NotNull(message = "Appointment time is required") LocalDateTime appointmentTime) {}
