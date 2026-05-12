package com.ironhack.application.usecase;

import com.ironhack.domain.DoctorEntity;
import com.ironhack.infra.adapter.output.DoctorRepository;
import com.ironhack.domain.Specialty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RegisterDoctorUseCase {

    private final DoctorRepository doctorRepository;

    public DoctorEntity execute(String fullName, Specialty specialty) {
        validateInput(fullName, specialty);
        validateDoctorDoesNotExist(fullName);

        return doctorRepository.save(
                DoctorEntity.builder()
                        .fullName(fullName)
                        .specialty(specialty)
                        .build()
        );
    }

    private void validateInput(String fullName, Specialty specialty) {
        if (Objects.isNull(fullName) || fullName.isBlank()) {
            throw new IllegalArgumentException("Doctor's full name is required and cannot be blank.");
        }

        if (Objects.isNull(specialty)) {
            throw new IllegalArgumentException("Doctor's specialty is required.");
        }

        if (fullName.length() < 3) {
            throw new IllegalArgumentException("Doctor's full name must contain at least 3 characters.");
        }
    }

    private void validateDoctorDoesNotExist(String fullName) {
        if (doctorRepository.existsByFullName(fullName)) {
            throw new IllegalArgumentException("A doctor with the name '" + fullName + "' already exists.");
        }
    }
}

