# Appointments Management Feature Documentation

## Overview
This feature provides comprehensive appointment management functionality for the Medicare system. It enables patients to schedule appointments with doctors and allows cancellation of scheduled appointments with proper validation and conflict prevention.

---

## 1. Book Appointment

### API Endpoint
- **URL**: `/v1/appointments`
- **Method**: `POST`
- **Content-Type**: `application/json`
- **Success Status Code**: `201 Created`

### Description
Allows patients to schedule new appointments with doctors. The system validates appointment time, verifies patient and doctor existence, and prevents double-booking of doctors.

### Request Body
```json
{
  "patientId": "550e8400-e29b-41d4-a716-446655440000",
  "doctorId": "550e8400-e29b-41d4-a716-446655440001",
  "appointmentTime": "2026-05-20T10:00:00"
}
```

### Request Validation Rules
| Field | Requirement | Details |
|-------|-------------|---------|
| `patientId` | Required | Must be valid UUID of existing patient |
| `doctorId` | Required | Must be valid UUID of existing doctor with specialty assigned |
| `appointmentTime` | Required | Must be in future, cannot be past date/time |

### Success Response (201)
```json
{
  "status": 201,
  "message": "Appointment booked successfully.",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "appointmentTime": "2026-05-20T10:00:00",
    "status": "SCHEDULED",
    "doctor": {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "fullName": "Dr. Ali Mammadov",
      "specialty": "CARDIOLOGY"
    },
    "patient": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "fullName": "John Doe",
      "phoneNumber": "201234567"
    }
  },
  "timestamp": "2026-05-13T10:00:00Z"
}
```

### Error Responses
- **400 Bad Request**: Invalid appointment time (past date/time)
- **404 Not Found**: Patient or doctor does not exist
- **409 Conflict**: Doctor has no specialty assigned OR doctor already has appointment at specified time

### Key Features
- ✅ Schedule appointments for future dates only
- ✅ Verify patient exists in system
- ✅ Verify doctor exists and has specialty assigned
- ✅ Prevent double-booking doctor appointments
- ✅ Automatic status set to SCHEDULED
- ✅ UUID-based tracking

---

## 2. Cancel Appointment

### API Endpoint
- **URL**: `/v1/appointments/{id}/cancel`
- **Method**: `POST`
- **Success Status Code**: `200 OK`

### Description
Allows cancellation of scheduled appointments. Only appointments with SCHEDULED status can be cancelled. This prevents double-cancellation and ensures data integrity.

### Request Parameters
| Parameter | Type | Requirement | Details |
|-----------|------|-------------|---------|
| `id` | path | Required | UUID of the appointment to cancel |

### Success Response (200)
```json
{
  "status": 200,
  "message": "Appointment cancelled successfully.",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "appointmentTime": "2026-05-20T10:00:00",
    "status": "CANCELLED",
    "doctor": {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "fullName": "Dr. Ali Mammadov",
      "specialty": "CARDIOLOGY"
    },
    "patient": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "fullName": "John Doe",
      "phoneNumber": "201234567"
    }
  },
  "timestamp": "2026-05-13T10:30:00Z"
}
```

### Error Responses
- **404 Not Found**: Appointment with specified ID does not exist
- **409 Conflict**: Appointment is not in SCHEDULED status (already cancelled or completed)

### Cancellation Rules
- ✅ Only SCHEDULED appointments can be cancelled
- ✅ Status changes from SCHEDULED to CANCELLED
- ✅ Completed appointments cannot be cancelled
- ✅ Already cancelled appointments cannot be cancelled again

### Key Features
- ✅ Cancel scheduled appointments by appointment ID
- ✅ Validate appointment exists
- ✅ Verify appointment status is SCHEDULED
- ✅ Update appointment status to CANCELLED
- ✅ Prevent re-cancellation of cancelled appointments
- ✅ Automatic timestamp on cancellation

---

## Appointment Statuses
| Status | Description |
|--------|-------------|
| `SCHEDULED` | Appointment is booked and confirmed |
| `CANCELLED` | Appointment has been cancelled |
| `COMPLETED` | Appointment has been completed |

## Common Error Codes
| Status Code | Error | Cause |
|-------------|-------|-------|
| 400 | Bad Request | Invalid appointment time (past date/time) |
| 404 | Not Found | Patient, doctor, or appointment does not exist |
| 409 | Conflict | Doctor scheduling conflict, doctor has no specialty, or invalid appointment status for cancellation |

