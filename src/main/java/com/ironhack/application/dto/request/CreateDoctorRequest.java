package com.ironhack.application.dto.request;

import com.ironhack.domain.Specialty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDoctorRequest(
        @NotBlank(message = "Doctor's full name is required")
        @Size(min = 3, message = "Doctor's full name must contain at least 3 characters")
        String fullName,

        @NotNull(message = "Doctor's specialty is required") Specialty specialty) {}
