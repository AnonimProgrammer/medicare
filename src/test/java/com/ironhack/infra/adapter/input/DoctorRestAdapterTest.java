package com.ironhack.infra.adapter.input;


import com.ironhack.application.dto.request.CreateDoctorRequest;
import com.ironhack.domain.Specialty;
import com.ironhack.infra.adapter.output.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DoctorRestAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        doctorRepository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully create doctor with valid data")
    void shouldCreateDoctorSuccessfully() throws Exception {
        CreateDoctorRequest request = new CreateDoctorRequest("Dr. Ali Mammadov", Specialty.CARDIOLOGY);

        mockMvc.perform(post("/v1/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Doctor created successfully."))
                .andExpect(jsonPath("$.data.fullName").value("Dr. Ali Mammadov"))
                .andExpect(jsonPath("$.data.specialty").value("CARDIOLOGY"));
    }

    @Test
    @DisplayName("Should return 400 when doctor name is blank")
    void shouldReturnValidationErrorWhenNameIsBlank() throws Exception {
        CreateDoctorRequest request = new CreateDoctorRequest("", Specialty.DENTIST);

        mockMvc.perform(post("/v1/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when doctor name is too short")
    void shouldReturnValidationErrorWhenNameIsTooShort() throws Exception {
        CreateDoctorRequest request = new CreateDoctorRequest("Dr", Specialty.THERAPIST);

        mockMvc.perform(post("/v1/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 409 when doctor with same name already exists")
    void shouldReturnConflictWhenDoctorNameAlreadyExists() throws Exception {
        CreateDoctorRequest request = new CreateDoctorRequest("Dr. Fatima Qasimova", Specialty.CARDIOLOGY);

        // Create first doctor
        mockMvc.perform(post("/v1/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Try to create doctor with same name
        mockMvc.perform(post("/v1/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

}

