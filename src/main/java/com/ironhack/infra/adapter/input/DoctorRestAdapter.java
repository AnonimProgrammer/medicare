package com.ironhack.infra.adapter.input;

import com.ironhack.application.dto.DoctorDTO;
import com.ironhack.application.dto.request.CreateDoctorRequest;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.usecase.CreateDoctorUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/doctors")
@RequiredArgsConstructor
public class DoctorRestAdapter {
    private final CreateDoctorUseCase createDoctorUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<DoctorDTO>> createDoctor(@Valid @RequestBody CreateDoctorRequest request) {
        ApiResponse<DoctorDTO> doctor = createDoctorUseCase.invoke(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(doctor);
    }
}
