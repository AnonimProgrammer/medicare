package com.ironhack.application.usecase.appointment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ironhack.application.dto.DoctorAppointmentDTO;
import com.ironhack.application.dto.query.AppointmentQueryCriteria;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.exception.NotFoundException;
import com.ironhack.domain.AppointmentEntity;
import com.ironhack.infra.adapter.mapper.MappingFacade;
import com.ironhack.infra.adapter.output.AppointmentRepository;
import com.ironhack.infra.adapter.output.DoctorRepository;
import com.ironhack.infra.adapter.output.specification.AppointmentSpecificationFactory;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListDoctorAppointmentsUseCase {
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentSpecificationFactory appointmentSpecificationFactory;
    private final MappingFacade mappingFacade;

    @Transactional(readOnly = true)
    public ApiResponse<List<DoctorAppointmentDTO>> invoke(UUID doctorId, AppointmentQueryCriteria criteria) {
        criteria.validateRange();
        ensureDoctorExists(doctorId);

        var spec = appointmentSpecificationFactory.forFilteredList(Optional.of(doctorId), Optional.empty(), criteria);
        List<AppointmentEntity> appointments =
                appointmentRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "appointmentTime"));
        List<DoctorAppointmentDTO> data = mappingFacade.toDoctorAppointmentDTOList(appointments);

        return ApiResponse.success(data, "Doctor appointments retrieved successfully.");
    }

    private void ensureDoctorExists(UUID doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new NotFoundException("Doctor not found.");
        }
    }
}
