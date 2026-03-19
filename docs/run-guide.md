# Run and Test Guide

## 1. Prerequisites
- Java 21+
- Maven Wrapper (`./mvnw`)
- Docker Desktop (for container run)

## 2. Run with Docker (recommended)
From `backend`:

```bash
./scripts/start-prod.sh
```

Stop:

```bash
./scripts/stop-prod.sh
```

## 3. Run without Docker (JVM)
From `backend`:

```bash
./scripts/start-all.sh
./scripts/status-all.sh
```

Stop:

```bash
./scripts/stop-all.sh
```

## 4. Health Checks

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8085/actuator/health
curl http://localhost:8888/actuator/health
curl http://localhost:8761/actuator/health
```

## 5. WebSocket Endpoints
- `ws://localhost:8080/ws/gateway`
- `ws://localhost:8082/signal`
- `ws://localhost:8083/ws/devices`
- `ws://localhost:8085/ws/transfers`
- `ws://localhost:8888/ws/config`
- `ws://localhost:8761/ws/discovery`

## 6. Logging Config Change
Edit:
- `backend/common-lib/src/main/resources/application.properties`

Then restart services to apply new destinations/filter/format/levels.
