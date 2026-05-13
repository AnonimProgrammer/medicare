package com.ironhack.infra.adapter.output;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ironhack.domain.AppointmentEntity;
import com.ironhack.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {
    boolean existsByDoctor_IdAndAppointmentTimeAndStatus(
            UUID doctorId, LocalDateTime appointmentTime, AppointmentStatus status);

    @EntityGraph(attributePaths = "doctor")
    List<AppointmentEntity> findByPatient_IdOrderByAppointmentTimeAsc(UUID patientId);

    @EntityGraph(attributePaths = "patient")
    List<AppointmentEntity> findByDoctor_IdOrderByAppointmentTimeAsc(UUID doctorId);

    @EntityGraph(attributePaths = {"patient", "doctor"})
    Optional<AppointmentEntity> findWithAssociationsById(UUID id);
}
