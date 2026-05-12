package com.ironhack.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePatientRequest {

    @NotBlank(message = "Patient's full name is required")
    private String fullName;

    @NotBlank(message = "Patient's phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,}$", message = "Phone number must be valid format")
    private String phoneNumber;
}

