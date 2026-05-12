package com.ironhack.infra.adapter.output;

import com.ironhack.domain.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, UUID> {

    boolean existsByFullName(String fullName);
}

