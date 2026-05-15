# MediCare

[![MediCare CI](https://github.com/AnonimProgrammer/medicare/actions/workflows/ci.yml/badge.svg)](https://github.com/AnonimProgrammer/medicare/actions/workflows/ci.yml) [![MediCare CD](https://github.com/AnonimProgrammer/medicare/actions/workflows/deploy.yml/badge.svg)](https://github.com/AnonimProgrammer/medicare/actions/workflows/deploy.yml)

A small clinic appointment system that lets a clinic manage patients, doctors, and their appointments while enforcing core scheduling rules.

## Live deployment

Continuous deployment runs from the [`deploy.yml`](https://github.com/AnonimProgrammer/medicare/actions/workflows/deploy.yml) workflow.

Simple web client: [MediCare UI](https://medicare-z79v.onrender.com)

## Project tracking

- [Jira (MED)](https://anonimprogrammer.atlassian.net/jira/software/projects/MED/list) — backlog and issues for this project.

## Documentation Guide

### Project Setup

- [Local setup](docs/local-setup.md) — run the app locally with Docker Compose.

### Architecture

- [System flow](docs/system-flow.md) — domain model, core flows, and simple business rules.
- [DTOs and MapStruct mappers](docs/dtos-and-mappers.md) — request/read DTOs, mappers, facade, and build notes.

### API documentation

- **Official reference** — [System flow](docs/system-flow.md) is the main human-readable contract: domain model, HTTP behaviour, status codes, and business rules. It is not tied to a particular environment.

- **OpenAPI** — The running app serves generated OpenAPI 3 JSON at `/v3/api-docs` (see `springdoc.api-docs.path` in `application.properties`). Use [Deployed OpenAPI](https://medicare-z79v.onrender.com/v3/api-docs) for production, or [Local OpenAPI](http://localhost:8080/v3/api-docs) when the app runs on your machine.

- **Interactive Swagger UI** — Explorer at `/swagger-ui.html` (see `springdoc.swagger-ui.path` in `application.properties`). Use [Deployed Swagger UI](https://medicare-z79v.onrender.com/swagger-ui.html) in production, or [Local Swagger UI](http://localhost:8080/swagger-ui.html) for a quick local try-out (not a substitute for the official reference).

- **Bruno collection** — The [Bruno](https://www.usebruno.com/) collection lives under [bruno/](bruno/): runnable requests grouped by resource. Pick an environment in Bruno: **local** uses [local.bru](bruno/environments/local.bru) with base URL [Local API](http://localhost:8080); **prod** uses [prod.bru](bruno/environments/prod.bru) with base URL [Deployed API](https://medicare-z79v.onrender.com) (same host as [MediCare UI](#live-deployment)).
