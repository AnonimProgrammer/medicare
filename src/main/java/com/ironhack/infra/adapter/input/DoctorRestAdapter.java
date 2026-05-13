package com.ironhack.infra.adapter.input;

import com.ironhack.application.dto.DoctorDTO;
import com.ironhack.application.dto.request.AssignDoctorSpecialtyRequest;
import com.ironhack.application.dto.request.CreateDoctorRequest;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.usecase.AssignSpecialtyToDoctorUseCase;
import com.ironhack.application.usecase.CreateDoctorUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/doctors")
@RequiredArgsConstructor
public class DoctorRestAdapter {
    private final CreateDoctorUseCase createDoctorUseCase;
    private final AssignSpecialtyToDoctorUseCase assignSpecialtyToDoctorUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<DoctorDTO>> createDoctor(@Valid @RequestBody CreateDoctorRequest request) {
        ApiResponse<DoctorDTO> doctor = createDoctorUseCase.invoke(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(doctor);
    }

    @PatchMapping("/{id}/specialty")
    public ResponseEntity<ApiResponse<DoctorDTO>> updateDoctor(@PathVariable UUID id, @Valid @RequestBody AssignDoctorSpecialtyRequest request) {
        ApiResponse<DoctorDTO> body=assignSpecialtyToDoctorUseCase.invoke(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
