package com.ironhack.application.dto.request;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.ironhack.infra.config.jackson.FlexibleOffsetDateTimeDeserializer;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.annotation.JsonDeserialize;

public record BookAppointmentRequest(
        @NotNull(message = "Patient ID is required") UUID patientId,
        @NotNull(message = "Doctor ID is required") UUID doctorId,

        @NotNull(message = "Appointment time is required")
        @JsonDeserialize(using = FlexibleOffsetDateTimeDeserializer.class)
        OffsetDateTime appointmentTime) {}
