package com.ironhack.application.dto;

import com.ironhack.domain.Specialty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

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

