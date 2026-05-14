# Local Setup

Run MediCare locally with Docker Compose.

## Prerequisites

- Docker
- Docker Compose

## Steps

1. Copy the example environment file:

   ```bash
   cp .env.example .env
   ```

2. Build and start the app together with MySQL:

   ```bash
   docker compose up --build
   ```

3. The API will be available at [http://localhost:8080](http://localhost:8080). MySQL is exposed on host port `3307`.

## REST requests (Bruno)

The [Bruno](https://www.usebruno.com/) collection in `bruno/` calls `{{base_url}}/v1/...`. After the stack is up, choose the **local** environment (`bruno/environments/local.bru`) so `base_url` is `http://localhost:8080`. To hit the public deployment instead, choose **prod** (`bruno/environments/prod.bru`) with `base_url` `https://medicare-z79v.onrender.com`.

## Stopping the Stack

To stop and remove the containers:

```bash
docker compose down
```

To also remove the database volume (fresh DB on next start):

```bash
docker compose down -v
```
