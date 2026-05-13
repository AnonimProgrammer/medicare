package com.ironhack.application.usecase.appointment;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ironhack.application.dto.DoctorAppointmentDTO;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.exception.NotFoundException;
import com.ironhack.domain.AppointmentEntity;
import com.ironhack.infra.adapter.mapper.MappingFacade;
import com.ironhack.infra.adapter.output.AppointmentRepository;
import com.ironhack.infra.adapter.output.DoctorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListDoctorAppointmentsUseCase {
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final MappingFacade mappingFacade;

    @Transactional(readOnly = true)
    public ApiResponse<List<DoctorAppointmentDTO>> invoke(UUID doctorId) {
        ensureDoctorExists(doctorId);

        List<AppointmentEntity> appointments = appointmentRepository.findByDoctor_IdOrderByAppointmentTimeAsc(doctorId);
        List<DoctorAppointmentDTO> data = mappingFacade.toDoctorAppointmentDTOList(appointments);

        return ApiResponse.success(data, "Doctor appointments retrieved successfully.");
    }

    private void ensureDoctorExists(UUID doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new NotFoundException("Doctor not found.");
        }
    }
}
