package com.ironhack.infra.adapter.output;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.ironhack.domain.AppointmentEntity;
import com.ironhack.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository
        extends JpaRepository<AppointmentEntity, UUID>, JpaSpecificationExecutor<AppointmentEntity> {
    boolean existsByDoctor_IdAndAppointmentTimeAndStatus(
            UUID doctorId, LocalDateTime appointmentTime, AppointmentStatus status);

    @Modifying
    void deleteByPatient_Id(UUID patientId);

    @Modifying
    void deleteByDoctor_Id(UUID doctorId);

    @EntityGraph(attributePaths = {"patient", "doctor"})
    Optional<AppointmentEntity> findWithAssociationsById(UUID id);
}
