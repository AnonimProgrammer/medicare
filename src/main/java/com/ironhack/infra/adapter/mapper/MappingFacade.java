package com.ironhack.infra.adapter.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ironhack.application.dto.AppointmentDTO;
import com.ironhack.application.dto.DoctorAppointmentDTO;
import com.ironhack.application.dto.DoctorDTO;
import com.ironhack.application.dto.PatientAppointmentDTO;
import com.ironhack.application.dto.PatientDTO;
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

    public DoctorEntity toDoctorEntity(CreateDoctorRequest request) {
        return doctorMapper.toDoctorEntity(request);
    }

    public PatientDTO toPatientDTO(PatientEntity entity) {
        return patientMapper.toPatientDTO(entity);
    }

    public PatientEntity toPatientEntity(CreatePatientRequest request) {
        return patientMapper.toPatientEntity(request);
    }

    public AppointmentDTO toAppointmentDTO(AppointmentEntity entity) {
        return appointmentMapper.toAppointmentDTO(entity);
    }

    public List<PatientAppointmentDTO> toPatientAppointmentDTOList(List<AppointmentEntity> entities) {
        return appointmentMapper.toPatientAppointmentDTOList(entities);
    }

    public List<DoctorAppointmentDTO> toDoctorAppointmentDTOList(List<AppointmentEntity> entities) {
        return appointmentMapper.toDoctorAppointmentDTOList(entities);
    }
}
