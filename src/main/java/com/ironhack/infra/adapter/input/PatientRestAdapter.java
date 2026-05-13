package com.ironhack.infra.adapter.input;

import com.ironhack.application.dto.PatientDTO;
import com.ironhack.application.dto.request.CreatePatientRequest;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.usecase.CreatePatientUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/patients")
@RequiredArgsConstructor
public class PatientRestAdapter {
    private final CreatePatientUseCase createPatientUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<PatientDTO>> createPatient(
            @Valid @RequestBody CreatePatientRequest request) {
        ApiResponse<PatientDTO> body = createPatientUseCase.invoke(request);
        return ResponseEntity.ok(body);
    }
}
