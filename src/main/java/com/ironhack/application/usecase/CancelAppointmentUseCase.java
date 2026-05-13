package com.ironhack.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

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
public class CancelAppointmentUseCase {
    private final AppointmentRepository appointmentRepository;
    private final MappingFacade mappingFacade;

    public ApiResponse<AppointmentDTO> invoke(UUID appointmentId) {
        AppointmentEntity appointment = requireScheduledAppointment(appointmentId);

        appointment.setStatus(AppointmentStatus.CANCELLED);
        AppointmentEntity saved = appointmentRepository.save(appointment);
        AppointmentDTO dto = mappingFacade.toAppointmentDTO(saved);

        return ApiResponse.success(dto, "Appointment cancelled successfully.");
    }

    private AppointmentEntity requireScheduledAppointment(UUID appointmentId) {
        AppointmentEntity appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found."));
        ensureAppointmentIsScheduled(appointment);
        return appointment;
    }

    private void ensureAppointmentIsScheduled(AppointmentEntity appointment) {
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new ConflictException("Cannot cancel appointment: only scheduled appointments may be cancelled.");
        }
    }
}
