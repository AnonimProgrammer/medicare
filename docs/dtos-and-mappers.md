# DTOs and MapStruct mappers

The API boundary uses request DTOs with Bean Validation, read models as DTOs, and MapStruct interfaces (plus a small facade) to translate between the web layer and JPA entities under `com.ironhack.domain`.

---

## Package layout

**Application DTOs** — `com.ironhack.application.dto`

```
dto/
├── DoctorDTO.java
├── PatientDTO.java
├── AppointmentDTO.java
├── request/
│   ├── RegisterDoctorRequest.java
│   ├── CreatePatientRequest.java
│   └── CreateAppointmentRequest.java
└── response/
    └── ApiResponse.java
```

**Mappers and facade** — `com.ironhack.infra.adapter.mapper`

```
mapper/
├── DoctorMapper.java
├── PatientMapper.java
├── AppointmentMapper.java
└── MappingFacade.java
```

**Shared MapStruct defaults** — `com.ironhack.infra.config.MapStructConfig`

---

## Read DTOs

| Class | Purpose |
|-------|---------|
| `DoctorDTO` | Responses: `id`, `fullName`, `specialty` |
| `PatientDTO` | Responses: `id`, `fullName`, `phoneNumber`, nested `appointments` when needed |
| `AppointmentDTO` | Responses: identifiers, time, `status`, optional nested `doctor` / `patient` |

All use Lombok (`@Getter`, `@Setter`, `@Builder`, constructors) to keep mapping targets small.

---

## Request DTOs and validation

| Class | Validation highlights |
|-------|------------------------|
| `RegisterDoctorRequest` | `@NotBlank` fullName, `@NotNull` specialty |
| `CreatePatientRequest` | `@NotBlank` names, `@Pattern` on phone |
| `CreateAppointmentRequest` | `@NotNull` ids and status, `@FutureOrPresent` on `appointmentTime` |

Typical HTTP mapping (illustrative): `POST /doctors`, `POST /patients`, `POST /appointments` with `@Valid` on the request body.

---

## `ApiResponse<T>`

`com.ironhack.application.dto.response.ApiResponse` wraps payloads with `status`, `message`, `data`, optional `errorCode`, and `timestamp`, with helpers such as `success`, `created`, and `error`. Null fields are omitted from JSON via `@JsonInclude(NON_NULL)`.

---

## MapStruct configuration

Shared settings live on `MapStructConfig`:

```java
@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    unmappedSourcePolicy = ReportingPolicy.WARN,
    typeConversionPolicy = ReportingPolicy.WARN
)
public interface MapStructConfig { }
```

Every mapper references this via `@Mapper(config = MapStructConfig.class, ...)`.

---

## Mapper interfaces

### `DoctorMapper`

- `DoctorDTO toDoctorDTO(DoctorEntity)`
- `DoctorEntity toDoctorEntity(DoctorDTO)` — ignores `appointments` on create-style paths
- `DoctorEntity toDoctoEntity(RegisterDoctorRequest)` — ignores `id`, `appointments` (note: method name as implemented)
- `void updateDoctorEntityFromRequest(RegisterDoctorRequest, @MappingTarget DoctorEntity)`

### `PatientMapper`

Uses `AppointmentMapper` for nested appointment graphs.

- `PatientDTO toPatientDTO(PatientEntity)`
- `PatientEntity toPatientEntity(PatientDTO)`
- `PatientEntity toPatientEntity(CreatePatientRequest)` — ignores `id`, `appointments` for create-style mapping
- `void updatePatientEntityFromRequest(CreatePatientRequest, @MappingTarget PatientEntity)`

### `AppointmentMapper`

Uses `DoctorMapper` and defines `@Named` helpers so foreign keys become lightweight entity stubs instead of loading full graphs during mapping.

- `AppointmentDTO toAppointmentDTO(AppointmentEntity)` — maps nested `patient.id` / `doctor.id` to `patientId` / `doctorId`
- `AppointmentEntity toAppointmentEntity(AppointmentDTO)`
- `AppointmentEntity toAppointmentEntity(CreateAppointmentRequest)` — ignores generated `id`
- `void updateAppointmentEntityFromRequest(CreateAppointmentRequest, @MappingTarget AppointmentEntity)`

Lazy reference example (from `AppointmentMapper`):

```java
@Named("patientIdToPatient")
default PatientEntity patientIdToPatient(UUID patientId) {
    if (patientId == null) {
        return null;
    }
    return PatientEntity.builder().id(patientId).build();
}
```

Update-style mapping example (doctor):

```java
@Mapping(target = "id", ignore = true)
@Mapping(target = "appointments", ignore = true)
void updateDoctorEntityFromRequest(
    RegisterDoctorRequest request,
    @MappingTarget DoctorEntity doctorEntity
);
```

---

## `MappingFacade`

`MappingFacade` is a Spring `@Component` that injects all mappers and exposes typed helpers (including list/stream helpers) so services and future controllers depend on one type instead of three mapper interfaces. Partial updates remain on the mapper interfaces (`update*FromRequest` with `@MappingTarget`) if a use case needs them before the facade grows.

```java
@Component
@RequiredArgsConstructor
public class MappingFacade {
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;
    private final AppointmentMapper appointmentMapper;
    // … facade methods …
}
```

---

## Request-to-persistence flow

```
HTTP request (@Valid)
        ↓
Request DTO (validation annotations)
        ↓
MapStruct mapper → domain entity
        ↓
Repository / use case
        ↓
Entity → read DTO
        ↓
ApiResponse<T> → JSON
```

---

## Example traces

**Create doctor (illustrative)**

```
POST /api/v1/doctors
{ "fullName": "Dr. John Smith", "specialty": "CARDIOLOGY" }
      ↓
RegisterDoctorRequest (@Valid)
      ↓
DoctorMapper.toDoctoEntity(request)
      ↓
DoctorEntity persisted
      ↓
DoctorMapper.toDoctorDTO(saved)
      ↓
ApiResponse<DoctorDTO> (e.g. 201 + message + data + timestamp)
```

**List appointments (illustrative)**

```
GET /api/v1/appointments
      ↓
loaded AppointmentEntity rows
      ↓
AppointmentMapper → List<AppointmentDTO>
      ↓
ApiResponse<List<AppointmentDTO>>
```

---

## Wiring mappers in services and controllers

Services typically accept request DTOs, call the facade or a mapper to build entities, persist, then map back to read DTOs wrapped in `ApiResponse`.

Controllers can stay thin: validate the body, delegate to a use case, map results:

```java
@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final MappingFacade mappingFacade;

    @PostMapping
    public ResponseEntity<ApiResponse<DoctorDTO>> create(
            @Valid @RequestBody RegisterDoctorRequest request) {
        // mappingFacade → entity → save → mappingFacade.toDoctorDTO(saved)
        // return ApiResponse.created(...)
    }
}
```

---

## Build tooling

MapStruct and Lombok annotation processing are enabled in `pom.xml`. Unmapped target properties fail the build (`ReportingPolicy.ERROR`), which keeps mapper interfaces aligned with DTO and entity fields as they evolve.

---

## Possible next steps

- Pagination metadata on list endpoints
- Read-side caching where response shapes are stable

For domain rules and flows, see [System flow](system-flow.md).
