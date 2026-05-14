package com.ironhack.application.usecase.appointment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ironhack.application.dto.AppointmentDTO;
import com.ironhack.application.dto.query.AppointmentQueryCriteria;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.domain.AppointmentEntity;
import com.ironhack.infra.adapter.mapper.MappingFacade;
import com.ironhack.infra.adapter.output.AppointmentRepository;
import com.ironhack.infra.adapter.output.specification.AppointmentSpecificationFactory;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchAppointmentsUseCase {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentSpecificationFactory appointmentSpecificationFactory;
    private final MappingFacade mappingFacade;

    @Transactional(readOnly = true)
    public ApiResponse<List<AppointmentDTO>> invoke(AppointmentQueryCriteria criteria) {
        criteria.validateRange();
        var spec = appointmentSpecificationFactory.forFilteredList(Optional.empty(), Optional.empty(), criteria);
        List<AppointmentEntity> appointments =
                appointmentRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "appointmentTime"));

        return ApiResponse.success(
                mappingFacade.toAppointmentDTOList(appointments), "Appointments retrieved successfully.");
    }
}
