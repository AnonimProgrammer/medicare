package com.ironhack.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreatePatientRequest(
        @NotBlank(message = "Patient's full name is required")
        String fullName,

        @NotBlank(message = "Patient's phone number is required")
        @Pattern(regexp = "^[+]?[0-9]{10,}$", message = "Phone number must be valid format")
        String phoneNumber) {}
