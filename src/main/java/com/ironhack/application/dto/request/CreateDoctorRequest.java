package com.ironhack.application.dto.request;

import com.ironhack.domain.Specialty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDoctorRequest {
    @NotBlank(message = "Doctor's full name is required")
    @Size(min = 3, message = "Doctor's full name must contain at least 3 characters")
    private String fullName;

    @NotNull(message = "Doctor's specialty is required")
    private Specialty specialty;
}
