package com.ironhack.application.dto;

import java.util.UUID;

import com.ironhack.domain.Specialty;
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
public class DoctorDTO {
    private UUID id;
    private String fullName;
    private Specialty specialty;
}
