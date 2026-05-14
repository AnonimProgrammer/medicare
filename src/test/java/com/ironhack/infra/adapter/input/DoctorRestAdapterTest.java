package com.ironhack.infra.adapter.input;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.ironhack.application.dto.request.BookAppointmentRequest;
import com.ironhack.application.dto.request.CreateDoctorRequest;
import com.ironhack.domain.AppointmentEntity;
import com.ironhack.domain.AppointmentStatus;
import com.ironhack.domain.DoctorEntity;
import com.ironhack.domain.PatientEntity;
import com.ironhack.domain.Specialty;
import com.ironhack.infra.adapter.output.AppointmentRepository;
import com.ironhack.infra.adapter.output.DoctorRepository;
import com.ironhack.infra.adapter.output.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
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

    // ==================== List Doctor Appointments Tests ====================

    @Test
    @DisplayName("Should return doctor appointments ordered by time, with distinct patients and no nested doctor field")
    void shouldListDoctorAppointmentsOrderedByTime() throws Exception {
        DoctorEntity doctor = doctorRepository.save(DoctorEntity.builder()
                .fullName("Dr. Ali Mammadov")
                .specialty(Specialty.CARDIOLOGY)
                .build());

        PatientEntity firstPatient = patientRepository.save(PatientEntity.builder()
                .fullName("John Doe")
                .phoneNumber("201234567")
                .build());
        PatientEntity secondPatient = patientRepository.save(PatientEntity.builder()
                .fullName("Jane Roe")
                .phoneNumber("201234568")
                .build());

        LocalDateTime later = LocalDateTime.now().plusDays(14);
        LocalDateTime earlier = LocalDateTime.now().plusDays(7);

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookAppointmentRequest(firstPatient.getId(), doctor.getId(), later))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookAppointmentRequest(secondPatient.getId(), doctor.getId(), earlier))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/doctors/{id}/appointments", doctor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Doctor appointments retrieved successfully."))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].status").value("SCHEDULED"))
                .andExpect(jsonPath("$.data[0].patient.id")
                        .value(secondPatient.getId().toString()))
                .andExpect(jsonPath("$.data[0].doctor").doesNotExist())
                .andExpect(jsonPath("$.data[1].patient.id")
                        .value(firstPatient.getId().toString()));
    }

    @Test
    @DisplayName("Should return empty list when doctor has no appointments")
    void shouldReturnEmptyListWhenDoctorHasNoAppointments() throws Exception {
        DoctorEntity doctor = doctorRepository.save(DoctorEntity.builder()
                .fullName("Dr. Ali Mammadov")
                .specialty(Specialty.CARDIOLOGY)
                .build());

        mockMvc.perform(get("/v1/doctors/{id}/appointments", doctor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("Should return 404 when listing appointments for a non-existent doctor")
    void shouldReturnNotFoundWhenListingAppointmentsForUnknownDoctor() throws Exception {
        mockMvc.perform(get("/v1/doctors/{id}/appointments", UUID.randomUUID())).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete doctor when they have no appointments")
    void shouldDeleteDoctorWhenNoAppointments() throws Exception {
        DoctorEntity doctor = doctorRepository.save(DoctorEntity.builder()
                .fullName("Dr. To Remove")
                .specialty(Specialty.DENTIST)
                .build());

        mockMvc.perform(delete("/v1/doctors/{id}", doctor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Doctor deleted successfully."));

        mockMvc.perform(get("/v1/doctors/{id}/appointments", doctor.getId())).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent doctor")
    void shouldReturnNotFoundWhenDeletingUnknownDoctor() throws Exception {
        mockMvc.perform(delete("/v1/doctors/{id}", UUID.randomUUID())).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete doctor and remove all their appointments")
    void shouldDeleteDoctorAndCascadeAppointments() throws Exception {
        DoctorEntity doctor = doctorRepository.save(DoctorEntity.builder()
                .fullName("Dr. Busy")
                .specialty(Specialty.CARDIOLOGY)
                .build());
        PatientEntity patient = patientRepository.save(PatientEntity.builder()
                .fullName("Pat")
                .phoneNumber("+10000000003")
                .build());
        appointmentRepository.save(AppointmentEntity.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentTime(LocalDateTime.now().plusDays(2))
                .status(AppointmentStatus.COMPLETED)
                .build());

        mockMvc.perform(delete("/v1/doctors/{id}", doctor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Doctor deleted successfully."));

        assertFalse(doctorRepository.existsById(doctor.getId()));
        assertEquals(0, appointmentRepository.count());
    }
}
