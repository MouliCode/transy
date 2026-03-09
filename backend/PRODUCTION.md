# Backend Production Build and Run

This document defines the production build/deploy path for the backend microservices.

## 1) Prepare environment

Create production env file:

```bash
cp .env.production.example .env.production
```

Fill strong values in `.env.production`, especially:

- `WS_API_KEY`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`

## 2) Build for production

```bash
./scripts/build-prod.sh
```

What it does:

- Builds all Maven modules (`clean package -DskipTests`)
- Builds production Docker images using `docker/docker-compose.prod.yml`

## 3) Start production stack

```bash
./scripts/start-prod.sh
```

Starts:

- `api-gateway`
- `config-server`
- `discovery-server`
- `device-service`
- `signaling-service`
- `transfer-service`
- `postgres`
- `redis`
- `prometheus`

## 4) Stop production stack

```bash
./scripts/stop-prod.sh
```

## 5) Observability

Prometheus is available on:

- `http://localhost:9090`

Each service exposes:

- `/actuator/health`
- `/actuator/prometheus`

## 6) Notes

- Production stack uses PostgreSQL for `transfer-service` and Redis for distributed session/device coordination.
- Local dev shortcut (`scripts/start-all.sh`) still uses `transfer-service` local profile with H2.
