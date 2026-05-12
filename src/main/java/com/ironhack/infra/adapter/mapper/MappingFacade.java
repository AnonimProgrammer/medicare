package com.ironhack.infra.adapter.mapper;

import com.ironhack.application.dto.AppointmentDTO;
import com.ironhack.application.dto.DoctorDTO;
import com.ironhack.application.dto.PatientDTO;
import com.ironhack.application.dto.request.CreateAppointmentRequest;
import com.ironhack.application.dto.request.CreatePatientRequest;
import com.ironhack.application.dto.request.RegisterDoctorRequest;
import com.ironhack.domain.AppointmentEntity;
import com.ironhack.domain.DoctorEntity;
import com.ironhack.domain.PatientEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MappingFacade {

    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;
    private final AppointmentMapper appointmentMapper;

    // Doctor Mapping Operations
    public DoctorDTO toDoctorDTO(DoctorEntity entity) {
        return doctorMapper.toDoctorDTO(entity);
    }

    public List<DoctorDTO> toDoctorDTOs(List<DoctorEntity> entities) {
        return entities.stream().map(doctorMapper::toDoctorDTO).toList();
    }

    public DoctorEntity toDoctorEntity(DoctorDTO dto) {
        return doctorMapper.toDoctorEntity(dto);
    }

    public DoctorEntity toDoctorEntity(RegisterDoctorRequest request) {
        return doctorMapper.toDoctoEntity(request);
    }

    // Patient Mapping Operations
    public PatientDTO toPatientDTO(PatientEntity entity) {
        return patientMapper.toPatientDTO(entity);
    }

    public List<PatientDTO> toPatientDTOs(List<PatientEntity> entities) {
        return entities.stream().map(patientMapper::toPatientDTO).toList();
    }

    public PatientEntity toPatientEntity(PatientDTO dto) {
        return patientMapper.toPatientEntity(dto);
    }

    public PatientEntity toPatientEntity(CreatePatientRequest request) {
        return patientMapper.toPatientEntity(request);
    }

    // Appointment Mapping Operations
    public AppointmentDTO toAppointmentDTO(AppointmentEntity entity) {
        return appointmentMapper.toAppointmentDTO(entity);
    }

    public List<AppointmentDTO> toAppointmentDTOs(List<AppointmentEntity> entities) {
        return entities.stream().map(appointmentMapper::toAppointmentDTO).toList();
    }

    public AppointmentEntity toAppointmentEntity(AppointmentDTO dto) {
        return appointmentMapper.toAppointmentEntity(dto);
    }

    public AppointmentEntity toAppointmentEntity(CreateAppointmentRequest request) {
        return appointmentMapper.toAppointmentEntity(request);
    }
}

