package com.ironhack.infra.adapter.input;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ironhack.application.dto.DoctorAppointmentDTO;
import com.ironhack.application.dto.DoctorDTO;
import com.ironhack.application.dto.query.AppointmentQueryCriteria;
import com.ironhack.application.dto.request.AssignDoctorSpecialtyRequest;
import com.ironhack.application.dto.request.CreateDoctorRequest;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.usecase.appointment.ListDoctorAppointmentsUseCase;
import com.ironhack.application.usecase.doctor.AssignSpecialtyToDoctorUseCase;
import com.ironhack.application.usecase.doctor.CreateDoctorUseCase;
import com.ironhack.application.usecase.doctor.DeleteDoctorUseCase;
import com.ironhack.application.usecase.doctor.ListDoctorsUseCase;
import com.ironhack.domain.AppointmentStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/doctors")
@RequiredArgsConstructor
public class DoctorRestAdapter {
    private final CreateDoctorUseCase createDoctorUseCase;
    private final DeleteDoctorUseCase deleteDoctorUseCase;
    private final AssignSpecialtyToDoctorUseCase assignSpecialtyToDoctorUseCase;
    private final ListDoctorAppointmentsUseCase listDoctorAppointmentsUseCase;
    private final ListDoctorsUseCase listDoctorsUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> listDoctors() {
        ApiResponse<List<DoctorDTO>> body = listDoctorsUseCase.invoke();
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DoctorDTO>> createDoctor(@Valid @RequestBody CreateDoctorRequest request) {
        ApiResponse<DoctorDTO> doctor = createDoctorUseCase.invoke(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(doctor);
    }

    @PatchMapping("/{id}/specialty")
    public ResponseEntity<ApiResponse<DoctorDTO>> assignSpecialtyToDoctor(
            @PathVariable UUID id, @Valid @RequestBody AssignDoctorSpecialtyRequest request) {
        ApiResponse<DoctorDTO> body = assignSpecialtyToDoctorUseCase.invoke(id, request);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(@PathVariable UUID id) {
        ApiResponse<Void> body = deleteDoctorUseCase.invoke(id);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}/appointments")
    public ResponseEntity<ApiResponse<List<DoctorAppointmentDTO>>> listDoctorAppointments(
            @PathVariable UUID id,
            @RequestParam(name = "status", required = false) List<AppointmentStatus> status,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {
        var criteria = new AppointmentQueryCriteria(status, date, from, to);
        ApiResponse<List<DoctorAppointmentDTO>> body = listDoctorAppointmentsUseCase.invoke(id, criteria);
        return ResponseEntity.ok(body);
    }
}
