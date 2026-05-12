package com.ironhack.infra.adapter.output;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ironhack.domain.DoctorEntity;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, UUID> {
    boolean existsByFullName(String fullName);
}
