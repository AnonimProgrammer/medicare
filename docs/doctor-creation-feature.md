# Doctor Creation Feature Documentation

## Overview
This feature enables the registration of new doctors in the Medicare system. It provides a REST API endpoint to create and store doctor information with validation and duplicate prevention mechanisms.

## API Endpoint
- **URL**: `/v1/doctors`
- **Method**: `POST`
- **Content-Type**: `application/json`
- **Success Status Code**: `201 Created`

## Request Body
```json
{
  "fullName": "Dr. Ali Mammadov",
  "specialty": "CARDIOLOGY"
}
```

### Request Validation Rules
| Field | Requirement | Details |
|-------|-------------|---------|
| `fullName` | Required | Minimum 3 characters, cannot be blank |
| `specialty` | Required | Must be one of: `CARDIOLOGY`, `DENTIST`, `THERAPIST` |

## Response

### Success Response (201)
```json
{
  "status": 201,
  "message": "Doctor created successfully.",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "fullName": "Dr. Ali Mammadov",
    "specialty": "CARDIOLOGY"
  },
  "timestamp": "2026-05-13T10:00:00Z"
}
```

### Error Responses
- **400 Bad Request**: Invalid input (name too short, blank fields)
- **409 Conflict**: Doctor with the same name already exists

## Key Features
- ✅ Create new doctor records with unique identification (UUID)
- ✅ Prevent duplicate doctor names in the system
- ✅ Input validation for all required fields
- ✅ Consistent error handling and response formatting
- ✅ Timestamp tracking for created records


