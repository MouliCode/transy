# Local Docker Run

Use this when you want to run backend services on your PC with Docker.

## 1) Start Docker daemon

Open Docker Desktop and wait for engine startup.

Check:

```bash
docker info
```

## 2) Start local stack

```bash
./scripts/start-docker-local.sh
```

What this does:

- Creates `.env.docker.local` from template if missing
- Builds images
- Starts all services + Postgres + Redis

## 3) Stop local stack

```bash
./scripts/stop-docker-local.sh
```

## 4) Endpoints

- API Gateway: `http://localhost:8080`
- Config Server: `http://localhost:8888`
- Device Service: `http://localhost:8083`
- Discovery Server: `http://localhost:8761`
- Signaling Service: `http://localhost:8082`
- Transfer Service: `http://localhost:8085`
