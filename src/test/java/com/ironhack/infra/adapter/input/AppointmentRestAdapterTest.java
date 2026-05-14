package com.ironhack.infra.adapter.input;

import java.time.OffsetDateTime;
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
        OffsetDateTime futureTime = OffsetDateTime.now().plusDays(7);
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
    @DisplayName("Should book appointment when appointment time is local ISO-8601 without offset")
    void shouldBookAppointmentWithLocalIsoDateTime() throws Exception {
        String body = """
                {
                  "patientId": "%s",
                  "doctorId": "%s",
                  "appointmentTime": "2099-01-15T14:30"
                }
                """.formatted(patientId, doctorId);

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("SCHEDULED"));
    }

    @Test
    @DisplayName("Should return 400 when booking appointment with past time")
    void shouldReturnBadRequestWhenAppointmentTimeInPast() throws Exception {
        OffsetDateTime pastTime = OffsetDateTime.now().minusDays(1);
        BookAppointmentRequest request = new BookAppointmentRequest(patientId, doctorId, pastTime);

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 404 when patient does not exist")
    void shouldReturnNotFoundWhenPatientDoesNotExist() throws Exception {
        OffsetDateTime futureTime = OffsetDateTime.now().plusDays(7);
        BookAppointmentRequest request = new BookAppointmentRequest(UUID.randomUUID(), doctorId, futureTime);

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when doctor does not exist")
    void shouldReturnNotFoundWhenDoctorDoesNotExist() throws Exception {
        OffsetDateTime futureTime = OffsetDateTime.now().plusDays(7);
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
        OffsetDateTime futureTime = OffsetDateTime.now().plusDays(7);
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
                objectMapper.readTree(response).get("data").get("id").asString();

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
        OffsetDateTime futureTime = OffsetDateTime.now().plusDays(7);
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
                objectMapper.readTree(response).get("data").get("id").asString();

        // Cancel the appointment first time
        mockMvc.perform(post("/v1/appointments/{id}/cancel", appointmentId)).andExpect(status().isOk());

        // Try to cancel again
        mockMvc.perform(post("/v1/appointments/{id}/cancel", appointmentId)).andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should complete a scheduled appointment")
    void shouldCompleteScheduledAppointment() throws Exception {
        OffsetDateTime futureTime = OffsetDateTime.now().plusDays(7);
        BookAppointmentRequest bookRequest = new BookAppointmentRequest(patientId, doctorId, futureTime);

        String response = mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String appointmentId =
                objectMapper.readTree(response).get("data").get("id").asString();

        mockMvc.perform(post("/v1/appointments/{id}/complete", appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Appointment completed successfully."))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Should return 404 when trying to complete non-existent appointment")
    void shouldReturnNotFoundWhenCompletingNonExistentAppointment() throws Exception {
        mockMvc.perform(post("/v1/appointments/{id}/complete", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 409 when trying to complete already completed appointment")
    void shouldReturnConflictWhenCompletingAlreadyCompletedAppointment() throws Exception {
        OffsetDateTime futureTime = OffsetDateTime.now().plusDays(7);
        BookAppointmentRequest bookRequest = new BookAppointmentRequest(patientId, doctorId, futureTime);

        String response = mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String appointmentId =
                objectMapper.readTree(response).get("data").get("id").asString();

        mockMvc.perform(post("/v1/appointments/{id}/complete", appointmentId)).andExpect(status().isOk());

        mockMvc.perform(post("/v1/appointments/{id}/complete", appointmentId)).andExpect(status().isConflict());
    }

    // ==================== List Patient Appointments Tests ====================

    @Test
    @DisplayName("Should return patient appointments ordered by time, without nested patient field")
    void shouldListPatientAppointmentsOrderedByTime() throws Exception {
        OffsetDateTime later = OffsetDateTime.now().plusDays(14);
        OffsetDateTime earlier = OffsetDateTime.now().plusDays(7);

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookAppointmentRequest(patientId, doctorId, later))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookAppointmentRequest(patientId, doctorId, earlier))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/patients/{id}/appointments", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Patient appointments retrieved successfully."))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].status").value("SCHEDULED"))
                .andExpect(jsonPath("$.data[0].doctor.id").value(doctorId.toString()))
                .andExpect(jsonPath("$.data[0].patient").doesNotExist())
                .andExpect(jsonPath("$.data[1].doctor.id").value(doctorId.toString()));
    }

    @Test
    @DisplayName("Should return empty list when patient has no appointments")
    void shouldReturnEmptyListWhenPatientHasNoAppointments() throws Exception {
        mockMvc.perform(get("/v1/patients/{id}/appointments", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("Should return 404 when listing appointments for a non-existent patient")
    void shouldReturnNotFoundWhenListingAppointmentsForUnknownPatient() throws Exception {
        mockMvc.perform(get("/v1/patients/{id}/appointments", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    // ==================== List Appointments (global) Tests ====================

    @Test
    @DisplayName("Should list appointments globally with patient and doctor populated")
    void shouldListAppointmentsGloballyOrderedByTime() throws Exception {
        OffsetDateTime later = OffsetDateTime.now().plusDays(14);
        OffsetDateTime earlier = OffsetDateTime.now().plusDays(7);

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookAppointmentRequest(patientId, doctorId, later))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookAppointmentRequest(patientId, doctorId, earlier))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Appointments retrieved successfully."))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].appointmentTime").exists())
                .andExpect(jsonPath("$.data[0].patient.id").value(patientId.toString()))
                .andExpect(jsonPath("$.data[0].doctor.id").value(doctorId.toString()))
                .andExpect(jsonPath("$.data[1].patient.id").value(patientId.toString()));
    }

    @Test
    @DisplayName("Should return empty list when no appointments exist globally")
    void shouldReturnEmptyListWhenNoAppointmentsGlobally() throws Exception {
        mockMvc.perform(get("/v1/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("Should filter global list by repeated status query params")
    void shouldFilterGlobalAppointmentsByStatus() throws Exception {
        OffsetDateTime t1 = OffsetDateTime.now().plusDays(5);
        OffsetDateTime t2 = OffsetDateTime.now().plusDays(6);

        mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookAppointmentRequest(patientId, doctorId, t1))))
                .andExpect(status().isCreated());

        String response = mockMvc.perform(post("/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookAppointmentRequest(patientId, doctorId, t2))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String secondId = objectMapper.readTree(response).get("data").get("id").asString();
        mockMvc.perform(post("/v1/appointments/{id}/cancel", secondId)).andExpect(status().isOk());

        mockMvc.perform(get("/v1/appointments").param("status", "SCHEDULED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].status").value("SCHEDULED"));

        mockMvc.perform(get("/v1/appointments").param("status", "SCHEDULED").param("status", "CANCELLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("Should return 400 when from is after to")
    void shouldReturnBadRequestWhenFromAfterTo() throws Exception {
        mockMvc.perform(get("/v1/appointments")
                        .param("from", "2026-06-01T12:00:00Z")
                        .param("to", "2026-06-01T08:00:00Z"))
                .andExpect(status().isBadRequest());
    }
}
