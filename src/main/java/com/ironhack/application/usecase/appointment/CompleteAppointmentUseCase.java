package com.ironhack.application.usecase.appointment;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ironhack.application.dto.AppointmentDTO;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.exception.ConflictException;
import com.ironhack.application.exception.NotFoundException;
import com.ironhack.domain.AppointmentEntity;
import com.ironhack.domain.AppointmentStatus;
import com.ironhack.infra.adapter.mapper.MappingFacade;
import com.ironhack.infra.adapter.output.AppointmentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompleteAppointmentUseCase {
    private final AppointmentRepository appointmentRepository;
    private final MappingFacade mappingFacade;

    @Transactional
    public ApiResponse<AppointmentDTO> invoke(UUID appointmentId) {
        AppointmentEntity appointment = requireScheduledAppointment(appointmentId);

        appointment.setStatus(AppointmentStatus.COMPLETED);
        AppointmentEntity saved = appointmentRepository.save(appointment);
        AppointmentDTO dto = mappingFacade.toAppointmentDTO(saved);

        return ApiResponse.success(dto, "Appointment completed successfully.");
    }

    private AppointmentEntity requireScheduledAppointment(UUID appointmentId) {
        AppointmentEntity appointment = appointmentRepository
                .findWithAssociationsById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found."));
        ensureAppointmentIsScheduled(appointment);
        return appointment;
    }

    private void ensureAppointmentIsScheduled(AppointmentEntity appointment) {
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new ConflictException("Cannot complete appointment: only scheduled appointments may be completed.");
        }
    }
}
