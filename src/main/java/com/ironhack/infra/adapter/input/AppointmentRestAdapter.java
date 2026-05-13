package com.ironhack.infra.adapter.input;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ironhack.application.dto.AppointmentDTO;
import com.ironhack.application.dto.request.BookAppointmentRequest;
import com.ironhack.application.dto.response.ApiResponse;
import com.ironhack.application.usecase.BookAppointmentUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/appointments")
@RequiredArgsConstructor
public class AppointmentRestAdapter {
    private final BookAppointmentUseCase bookAppointmentUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentDTO>> bookAppointment(
            @Valid @RequestBody BookAppointmentRequest request) {
        ApiResponse<AppointmentDTO> body = bookAppointmentUseCase.invoke(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}
