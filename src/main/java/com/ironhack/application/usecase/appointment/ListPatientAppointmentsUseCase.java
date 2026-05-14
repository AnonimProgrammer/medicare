package com.ironhack.application.usecase.appointment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ironhack.application.dto.PatientAppointmentDTO;
import com.ironhack.application.dto.query.AppointmentQueryCriteria;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.exception.NotFoundException;
import com.ironhack.domain.AppointmentEntity;
import com.ironhack.infra.adapter.mapper.MappingFacade;
import com.ironhack.infra.adapter.output.AppointmentRepository;
import com.ironhack.infra.adapter.output.PatientRepository;
import com.ironhack.infra.adapter.output.specification.AppointmentSpecificationFactory;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListPatientAppointmentsUseCase {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentSpecificationFactory appointmentSpecificationFactory;
    private final MappingFacade mappingFacade;

    @Transactional(readOnly = true)
    public ApiResponse<List<PatientAppointmentDTO>> invoke(UUID patientId, AppointmentQueryCriteria criteria) {
        criteria.validateRange();
        ensurePatientExists(patientId);

        var spec = appointmentSpecificationFactory.forFilteredList(Optional.empty(), Optional.of(patientId), criteria);
        List<AppointmentEntity> appointments =
                appointmentRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "appointmentTime"));
        List<PatientAppointmentDTO> data = mappingFacade.toPatientAppointmentDTOList(appointments);

        return ApiResponse.success(data, "Patient appointments retrieved successfully.");
    }

    private void ensurePatientExists(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new NotFoundException("Patient not found.");
        }
    }
}
