package com.ironhack.infra.adapter.input;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ironhack.application.dto.AppointmentDTO;
import com.ironhack.application.dto.query.AppointmentQueryCriteria;
import com.ironhack.application.dto.request.BookAppointmentRequest;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.usecase.appointment.BookAppointmentUseCase;
import com.ironhack.application.usecase.appointment.CancelAppointmentUseCase;
import com.ironhack.application.usecase.appointment.SearchAppointmentsUseCase;
import com.ironhack.domain.AppointmentStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/appointments")
@RequiredArgsConstructor
public class AppointmentRestAdapter {
    private final BookAppointmentUseCase bookAppointmentUseCase;
    private final CancelAppointmentUseCase cancelAppointmentUseCase;
    private final SearchAppointmentsUseCase searchAppointmentsUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> listAppointments(
            @RequestParam(name = "status", required = false) List<AppointmentStatus> status,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {
        var criteria = new AppointmentQueryCriteria(status, date, from, to);
        ApiResponse<List<AppointmentDTO>> body = searchAppointmentsUseCase.invoke(criteria);
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentDTO>> bookAppointment(
            @Valid @RequestBody BookAppointmentRequest request) {
        ApiResponse<AppointmentDTO> body = bookAppointmentUseCase.invoke(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<AppointmentDTO>> cancelAppointment(@PathVariable UUID id) {
        ApiResponse<AppointmentDTO> body = cancelAppointmentUseCase.invoke(id);
        return ResponseEntity.ok(body);
    }
}
