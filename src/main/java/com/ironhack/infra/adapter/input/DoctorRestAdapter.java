package com.ironhack.infra.adapter.input;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ironhack.application.dto.DoctorAppointmentDTO;
import com.ironhack.application.dto.DoctorDTO;
import com.ironhack.application.dto.request.AssignDoctorSpecialtyRequest;
import com.ironhack.application.dto.request.CreateDoctorRequest;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.usecase.appointment.ListDoctorAppointmentsUseCase;
import com.ironhack.application.usecase.doctor.AssignSpecialtyToDoctorUseCase;
import com.ironhack.application.usecase.doctor.CreateDoctorUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/doctors")
@RequiredArgsConstructor
public class DoctorRestAdapter {
    private final CreateDoctorUseCase createDoctorUseCase;
    private final AssignSpecialtyToDoctorUseCase assignSpecialtyToDoctorUseCase;
    private final ListDoctorAppointmentsUseCase listDoctorAppointmentsUseCase;

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

    @GetMapping("/{id}/appointments")
    public ResponseEntity<ApiResponse<List<DoctorAppointmentDTO>>> listDoctorAppointments(@PathVariable UUID id) {
        ApiResponse<List<DoctorAppointmentDTO>> body = listDoctorAppointmentsUseCase.invoke(id);
        return ResponseEntity.ok(body);
    }
}
