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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AppointmentRestAdapterTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID doctorId;
    private UUID patientId;
    private UUID appointmentId;

    @BeforeEach
    void setup() {
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();

        DoctorEntity doctor = DoctorEntity.builder()
                .fullName("Dr. Ali Mammadov")
                .specialty(Specialty.CARDIOLOGY)
                .build();
        DoctorEntity savedDoctor = doctorRepository.save(doctor);
        doctorId = savedDoctor.getId();

        PatientEntity patient = PatientEntity.builder()
                .fullName("John Doe")
                .phoneNumber("201234567")
                .build();
        PatientEntity savedPatient = patientRepository.save(patient);
        patientId = savedPatient.getId();
    }

    // ==================== Book Appointment Tests ====================

    @Test
    @DisplayName("Should successfully book appointment with valid data")
    void shouldBookAppointmentSuccessfully() throws Exception {
        LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
        BookAppointmentRequest request = new BookAppointmentRequest(patientId, doctorId, futureTime);

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Appointment booked successfully."))
                .andExpect(jsonPath("$.data.status").value("SCHEDULED"));
    }

    @Test
    @DisplayName("Should return 400 when booking appointment with past time")
    void shouldReturnBadRequestWhenAppointmentTimeInPast() throws Exception {
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        BookAppointmentRequest request = new BookAppointmentRequest(patientId, doctorId, pastTime);

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 404 when patient does not exist")
    void shouldReturnNotFoundWhenPatientDoesNotExist() throws Exception {
        LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
        BookAppointmentRequest request = new BookAppointmentRequest(UUID.randomUUID(), doctorId, futureTime);

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when doctor does not exist")
    void shouldReturnNotFoundWhenDoctorDoesNotExist() throws Exception {
        LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
        BookAppointmentRequest request = new BookAppointmentRequest(patientId, UUID.randomUUID(), futureTime);

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ==================== Cancel Appointment Tests ====================

    @Test
    @DisplayName("Should successfully cancel scheduled appointment")
    void shouldCancelAppointmentSuccessfully() throws Exception {
        LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
        BookAppointmentRequest bookRequest = new BookAppointmentRequest(patientId, doctorId, futureTime);

        // First, book an appointment
        String response = mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract appointment ID from response
        String appointmentId =
                objectMapper.readTree(response).get("data").get("id").asText();

        // Cancel the appointment
        mockMvc.perform(post("/v1/appointments/{id}/cancel", appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Appointment cancelled successfully."))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("Should return 404 when trying to cancel non-existent appointment")
    void shouldReturnNotFoundWhenAppointmentDoesNotExist() throws Exception {
        mockMvc.perform(post("/v1/appointments/{id}/cancel", UUID.randomUUID())).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 409 when trying to cancel already cancelled appointment")
    void shouldReturnConflictWhenAppointmentAlreadyCancelled() throws Exception {
        LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
        BookAppointmentRequest bookRequest = new BookAppointmentRequest(patientId, doctorId, futureTime);

        // Book an appointment
        String response = mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String appointmentId =
                objectMapper.readTree(response).get("data").get("id").asText();

        // Cancel the appointment first time
        mockMvc.perform(post("/v1/appointments/{id}/cancel", appointmentId)).andExpect(status().isOk());

        // Try to cancel again
        mockMvc.perform(post("/v1/appointments/{id}/cancel", appointmentId)).andExpect(status().isConflict());
    }
}
