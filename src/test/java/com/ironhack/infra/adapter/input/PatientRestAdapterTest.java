package com.ironhack.infra.adapter.input;

import com.ironhack.application.dto.request.BookAppointmentRequest;
import com.ironhack.domain.AppointmentStatus;
import com.ironhack.domain.DoctorEntity;
import com.ironhack.domain.PatientEntity;
import com.ironhack.domain.Specialty;
import com.ironhack.infra.adapter.output.AppointmentRepository;
import com.ironhack.infra.adapter.output.DoctorRepository;
import tools.jackson.databind.ObjectMapper;
import com.ironhack.application.dto.request.CreatePatientRequest;
import com.ironhack.infra.adapter.output.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PatientRestAdapterTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully create patient with valid data")
    void shouldSuccessfullyCreatePatientWithValidData() throws Exception {
        CreatePatientRequest request = new CreatePatientRequest("Cafar Babayev","+994501234567");

        mockMvc.perform(post("/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Patient created successfully."))
                .andExpect(jsonPath("$.data.fullName").value("Cafar Babayev"))
                .andExpect(jsonPath("$.data.phoneNumber").value("+994501234567"));
    }

    @Test
    @DisplayName("Should return 400 when patient full name is blank")
    void shouldReturnValidationErrorWhenFullNameIsBlank() throws Exception {
        CreatePatientRequest request = new CreatePatientRequest("", "+994501234567");

        mockMvc.perform(post("/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 409 when patient with same phone number already exists")
    void shouldReturnConflictWhenPatientPhoneNumberAlreadyExists() throws Exception {

        CreatePatientRequest request1 =
                new CreatePatientRequest("Cafar Babayev", "+994501234567");

        CreatePatientRequest request2 =
                new CreatePatientRequest("Ali Hasanov", "+994501234567");

        // Create first patient
        mockMvc.perform(post("/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Try to create another patient with same phone number
        mockMvc.perform(post("/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should list patient appointments successfully")
    void shouldListPatientAppointmentsSuccessfully() throws Exception {

        PatientEntity patient = patientRepository.save(
                PatientEntity.builder()
                        .fullName("Cafar Babayev")
                        .phoneNumber("+994501234567")
                        .build()
        );

        DoctorEntity doctor = doctorRepository.save(
                DoctorEntity.builder()
                        .fullName("Dr. Ali")
                        .specialty(Specialty.CARDIOLOGY)
                        .build()
        );

        LocalDateTime appointmentTime =
                LocalDateTime.now().plusDays(5);

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookAppointmentRequest(
                                        patient.getId(),
                                        doctor.getId(),
                                        appointmentTime
                                ))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/patients/{id}/appointments", patient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].status")
                        .value(AppointmentStatus.SCHEDULED.name()));
    }

    @Test
    @DisplayName("Should return empty list when patient has no appointments")
    void shouldReturnEmptyListWhenPatientHasNoAppointments() throws Exception {

        PatientEntity patient = patientRepository.save(
                PatientEntity.builder()
                        .fullName("Cafar Babayev")
                        .phoneNumber("+994501234567")
                        .build()
        );

        mockMvc.perform(get("/v1/patients/{id}/appointments", patient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("Should return 404 when patient does not exist")
    void shouldReturnNotFoundWhenPatientDoesNotExist() throws Exception {

        mockMvc.perform(get("/v1/patients/{id}/appointments", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
