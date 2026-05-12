# Medicare - DTOs and MapStruct Mappers Implementation

## Overview
Advanced DTO and MapStruct mapper implementation following senior software engineering practices with:
- Separation of concerns (Request, Response, DTO layers)
- Type-safe mapping using MapStruct
- Spring component model integration
- Validation annotations
- Builder pattern for object creation
- Facade pattern for centralized mapping control

---

## DTOs Created

### 1. **DoctorDTO** 
`com.ironhack.application.dto.DoctorDTO`
- Fields: `id`, `fullName`, `specialty`
- Used for read operations and API responses

### 2. **PatientDTO**
`com.ironhack.application.dto.PatientDTO`
- Fields: `id`, `fullName`, `phoneNumber`, `appointments`
- Includes bidirectional relationship with appointments
- Full object graph representation

### 3. **AppointmentDTO**
`com.ironhack.application.dto.AppointmentDTO`
- Fields: `id`, `patientId`, `doctorId`, `appointmentTime`, `status`
- Additional fields: `doctor`, `patient` (full objects)
- Flexible for different API response scenarios

---

## Request DTOs

### 1. **RegisterDoctorRequest**
`com.ironhack.application.dto.request.RegisterDoctorRequest`
- Validation: `@NotBlank`, `@NotNull`
- Used for POST /doctors endpoint
- Input validation at DTO level

### 2. **CreatePatientRequest**
`com.ironhack.application.dto.request.CreatePatientRequest`
- Validation: `@NotBlank`, `@Pattern` (phone format)
- Used for POST /patients endpoint
- Advanced phone number regex validation

### 3. **CreateAppointmentRequest**
`com.ironhack.application.dto.request.CreateAppointmentRequest`
- Validation: `@NotNull`, `@FutureOrPresent`
- Used for POST /appointments endpoint
- Temporal validation for appointment scheduling

---

## Response DTOs

### **ApiResponse<T>**
`com.ironhack.application.dto.response.ApiResponse`
- Generic response wrapper
- Fields: `status`, `message`, `data`, `errorCode`, `timestamp`
- Factory methods: `success()`, `created()`, `error()`
- Consistent API response format
- `@JsonInclude(NON_NULL)` for clean JSON serialization

---

## MapStruct Mappers

### 1. **DoctorMapper**
`com.ironhack.infra.adapter.mapper.DoctorMapper`
```
Operations:
- toDoctorDTO(DoctorEntity) → DoctorDTO
- toDoctorEntity(DoctorDTO) → DoctorEntity
- toDoctoEntity(RegisterDoctorRequest) → DoctorEntity
- updateDoctorEntityFromRequest(RegisterDoctorRequest, DoctorEntity) → void
```
- Ignores: `appointments` collection for creation flows
- Spring component model enabled

### 2. **PatientMapper**
`com.ironhack.infra.adapter.mapper.PatientMapper`
```
Operations:
- toPatientDTO(PatientEntity) → PatientDTO
- toPatientEntity(PatientDTO) → PatientEntity
- toPatientEntity(CreatePatientRequest) → PatientEntity
- updatePatientEntityFromRequest(CreatePatientRequest, PatientEntity) → void
```
- Uses AppointmentMapper for nested mapping
- Ignores: `id`, `appointments` for creation flows

### 3. **AppointmentMapper**
`com.ironhack.infra.adapter.mapper.AppointmentMapper`
```
Operations:
- toAppointmentDTO(AppointmentEntity) → AppointmentDTO (with ID extraction)
- toAppointmentEntity(AppointmentDTO) → AppointmentEntity
- toAppointmentEntity(CreateAppointmentRequest) → AppointmentEntity
- updateAppointmentEntityFromRequest(CreateAppointmentRequest, AppointmentEntity) → void

Custom Named Mappings:
- @Named("patientIdToPatient"): UUID → PatientEntity (creates lazy reference)
- @Named("doctorIdToDoctor"): UUID → DoctorEntity (creates lazy reference)
```
- Advanced UUID-to-Entity lazy loading
- Bidirectional relationship mapping

---

## MapStruct Configuration

### **MapStructMapperConfig**
`com.ironhack.infra.adapter.mapper.config.MapStructMapperConfig`
```java
@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    unmappedSourcePolicy = ReportingPolicy.WARN,
    typeConversionPolicy = ReportingPolicy.WARN
)
```
- Spring component model for dependency injection
- Strict error reporting for unmapped properties
- Warnings for unused source mappings

---

## Mapping Facade

### **MappingFacade**
`com.ironhack.infra.adapter.mapper.MappingFacade`
- Central facade for all mapping operations
- Convenience methods with Stream API support
- Single injection point for services/controllers
- Type-safe mapping operations
- Example usage:
  ```java
  @Autowired MappingFacade mappingFacade;
  
  DoctorDTO dto = mappingFacade.toDoctorDTO(doctorEntity);
  List<DoctorDTO> dtos = mappingFacade.toDoctorDTOs(entities);
  ```

---

## Advanced Features

### 1. **Bidirectional Mapping**
- Support for both Entity → DTO and DTO → Entity conversions
- Update operations using `@MappingTarget`

### 2. **Lazy Loading**
- UUID-to-Entity conversion avoids eager loading
- Creates minimal entity stubs for relationship establishment

### 3. **Composite Mapping**
- Nested mapper composition (e.g., PatientMapper uses AppointmentMapper)
- Automatic recursive mapping

### 4. **Validation**
- JSR-380 annotations on request DTOs
- Input validation before entity creation

### 5. **Generic Response Wrapper**
- Type-safe generic ApiResponse<T>
- Consistent error handling
- Timestamp tracking

---

## Compilation Status
✅ **Project compiles successfully** with MapStruct annotation processors enabled

## Architecture Diagram
```
Request Layer
     ↓
Request DTOs → DoctorMapper, PatientMapper, AppointmentMapper
     ↓
Service Layer (RegisterDoctorUseCase, etc.)
     ↓
Entity Layer (DoctorEntity, PatientEntity, AppointmentEntity)
     ↓
Persistence Layer
     ↓
DTOs → Response Wrapper (ApiResponse<T>) → JSON Response
```

---

## Best Practices Applied

✓ Single Responsibility Principle (SRP)
✓ Dependency Injection via Spring
✓ Builder pattern for DTOs
✓ Lombok for boilerplate reduction
✓ Strict compilation warnings
✓ Type safety with MapStruct
✓ Validation at API boundary
✓ Facade pattern for simplified usage
✓ Lazy loading for relationships
✓ Immutable data transfer objects

