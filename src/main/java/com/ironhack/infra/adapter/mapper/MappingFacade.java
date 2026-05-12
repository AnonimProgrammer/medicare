package com.ironhack.infra.adapter.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ironhack.application.dto.AppointmentDTO;
import com.ironhack.application.dto.DoctorDTO;
import com.ironhack.application.dto.PatientDTO;
import com.ironhack.application.dto.request.CreateAppointmentRequest;
import com.ironhack.application.dto.request.CreateDoctorRequest;
import com.ironhack.application.dto.request.CreatePatientRequest;
import com.ironhack.domain.AppointmentEntity;
import com.ironhack.domain.DoctorEntity;
import com.ironhack.domain.PatientEntity;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MappingFacade {
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;
    private final AppointmentMapper appointmentMapper;

    public DoctorDTO toDoctorDTO(DoctorEntity entity) {
        return doctorMapper.toDoctorDTO(entity);
    }

    public List<DoctorDTO> toDoctorDTOs(List<DoctorEntity> entities) {
        return entities.stream().map(doctorMapper::toDoctorDTO).toList();
    }

    public DoctorEntity toDoctorEntity(CreateDoctorRequest request) {
        return doctorMapper.toDoctorEntity(request);
    }

    public PatientDTO toPatientDTO(PatientEntity entity) {
        return patientMapper.toPatientDTO(entity);
    }

    public List<PatientDTO> toPatientDTOs(List<PatientEntity> entities) {
        return entities.stream().map(patientMapper::toPatientDTO).toList();
    }

    public PatientEntity toPatientEntity(CreatePatientRequest request) {
        return patientMapper.toPatientEntity(request);
    }

    public AppointmentDTO toAppointmentDTO(AppointmentEntity entity) {
        return appointmentMapper.toAppointmentDTO(entity);
    }

    public List<AppointmentDTO> toAppointmentDTOs(List<AppointmentEntity> entities) {
        return entities.stream().map(appointmentMapper::toAppointmentDTO).toList();
    }

    public AppointmentEntity toAppointmentEntity(CreateAppointmentRequest request) {
        return appointmentMapper.toAppointmentEntity(request);
    }
}
