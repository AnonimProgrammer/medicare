package com.ironhack.infra.adapter.input;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ironhack.application.dto.PatientAppointmentDTO;
import com.ironhack.application.dto.PatientDTO;
import com.ironhack.application.dto.query.AppointmentQueryCriteria;
import com.ironhack.application.dto.request.CreatePatientRequest;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.usecase.appointment.ListPatientAppointmentsUseCase;
import com.ironhack.application.usecase.patient.CreatePatientUseCase;
import com.ironhack.application.usecase.patient.DeletePatientUseCase;
import com.ironhack.application.usecase.patient.ListPatientsUseCase;
import com.ironhack.domain.AppointmentStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/patients")
@RequiredArgsConstructor
public class PatientRestAdapter {
    private final CreatePatientUseCase createPatientUseCase;
    private final DeletePatientUseCase deletePatientUseCase;
    private final ListPatientAppointmentsUseCase listPatientAppointmentsUseCase;
    private final ListPatientsUseCase listPatientsUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PatientDTO>>> listPatients() {
        ApiResponse<List<PatientDTO>> body = listPatientsUseCase.invoke();
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PatientDTO>> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        ApiResponse<PatientDTO> body = createPatientUseCase.invoke(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable UUID id) {
        ApiResponse<Void> body = deletePatientUseCase.invoke(id);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}/appointments")
    public ResponseEntity<ApiResponse<List<PatientAppointmentDTO>>> listPatientAppointments(
            @PathVariable UUID id,
            @RequestParam(name = "status", required = false) List<AppointmentStatus> status,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {
        var criteria = new AppointmentQueryCriteria(status, date, from, to);
        ApiResponse<List<PatientAppointmentDTO>> body = listPatientAppointmentsUseCase.invoke(id, criteria);
        return ResponseEntity.ok(body);
    }
}
