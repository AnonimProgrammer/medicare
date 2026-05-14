package com.ironhack.application.dto;

import com.ironhack.domain.Specialty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DoctorDTO extends BaseDto {
    private String fullName;
    private Specialty specialty;
}
