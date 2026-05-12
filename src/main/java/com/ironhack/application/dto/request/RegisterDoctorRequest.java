package com.ironhack.application.dto.request;

import com.ironhack.domain.Specialty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDoctorRequest {

    @NotBlank(message = "Doctor's full name is required")
    private String fullName;

    @NotNull(message = "Doctor's specialty is required")
    private Specialty specialty;
}

