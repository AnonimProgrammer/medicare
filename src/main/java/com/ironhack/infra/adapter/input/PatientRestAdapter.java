package com.ironhack.infra.adapter.input;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ironhack.application.dto.PatientAppointmentDTO;
import com.ironhack.application.dto.PatientDTO;
import com.ironhack.application.dto.request.CreatePatientRequest;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.usecase.appointment.ListPatientAppointmentsUseCase;
import com.ironhack.application.usecase.patient.CreatePatientUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/patients")
@RequiredArgsConstructor
public class PatientRestAdapter {
    private final CreatePatientUseCase createPatientUseCase;
    private final ListPatientAppointmentsUseCase listPatientAppointmentsUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<PatientDTO>> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        ApiResponse<PatientDTO> body = createPatientUseCase.invoke(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{id}/appointments")
    public ResponseEntity<ApiResponse<List<PatientAppointmentDTO>>> listPatientAppointments(@PathVariable UUID id) {
        ApiResponse<List<PatientAppointmentDTO>> body = listPatientAppointmentsUseCase.invoke(id);
        return ResponseEntity.ok(body);
    }
}
