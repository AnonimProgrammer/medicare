package com.ironhack.application.dto.request;

import com.ironhack.domain.Specialty;
import jakarta.validation.constraints.NotNull;

public record AssignDoctorSpecialtyRequest(
        @NotNull(message = "Specialty is required") Specialty specialty) {}
