package com.ironhack.infra.adapter.output;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ironhack.domain.PatientEntity;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, UUID> {
    boolean existsByPhoneNumber(String phoneNumber);
}
