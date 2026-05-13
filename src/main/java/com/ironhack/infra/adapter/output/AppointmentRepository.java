package com.ironhack.infra.adapter.output;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ironhack.domain.AppointmentEntity;
import com.ironhack.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {
    boolean existsByDoctor_IdAndAppointmentTimeAndStatus(
            UUID doctorId, LocalDateTime appointmentTime, AppointmentStatus status);
}
