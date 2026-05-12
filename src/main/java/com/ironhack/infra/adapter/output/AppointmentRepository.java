package com.ironhack.infra.adapter.output;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ironhack.domain.AppointmentEntity;
import com.ironhack.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {
    List<AppointmentEntity> findByPatientId(UUID patientId);

    List<AppointmentEntity> findByDoctorId(UUID doctorId);

    List<AppointmentEntity> findByStatus(AppointmentStatus status);

    List<AppointmentEntity> findByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end);

    List<AppointmentEntity> findByPatientIdAndStatus(UUID patientId, AppointmentStatus status);

    List<AppointmentEntity> findByDoctorIdAndStatus(UUID doctorId, AppointmentStatus status);
}
