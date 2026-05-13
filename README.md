# MediCare

[![MediCare CI](https://github.com/AnonimProgrammer/medicare/actions/workflows/ci.yml/badge.svg)](https://github.com/AnonimProgrammer/medicare/actions/workflows/ci.yml)

A small clinic appointment system that lets a clinic manage patients, doctors, and their appointments while enforcing core scheduling rules.

## Documentation Guide

### Project Setup

- [Local setup](docs/local-setup.md) — run the app locally with Docker Compose.

### Architecture

- [System flow](docs/foundation-flow.md) — domain model, core flows, and simple business rules.
- [DTOs and MapStruct mappers](docs/dtos-and-mappers.md) — request/read DTOs, mappers, facade, and build notes.

### API documentation

- **OpenAPI (Swagger)** — With the app running, [Swagger UI](http://localhost:8080/swagger-ui.html) lists every endpoint and schemas inferred from the Java controllers and DTOs.
- [Bruno collection](bruno/) — runnable HTTP requests for the REST API, grouped by resource. Open the `bruno` folder in [Bruno](https://www.usebruno.com/), pick an environment (`environments/local.bru` uses `http://localhost:8080`; `prod` leaves `base_url` empty).
