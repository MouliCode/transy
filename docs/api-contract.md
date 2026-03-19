# API Contract

This backend is primarily websocket-driven. HTTP is used for actuator/ops checks.

## 1) HTTP Endpoints

### Health
- `GET /actuator/health` on each service:
  - gateway: `http://localhost:8080/actuator/health`
  - signaling: `http://localhost:8082/actuator/health`
  - device: `http://localhost:8083/actuator/health`
  - transfer: `http://localhost:8085/actuator/health`
  - config: `http://localhost:8888/actuator/health`
  - discovery: `http://localhost:8761/actuator/health`

Response:
```json
{
  "status": "UP",
  "groups": ["liveness", "readiness"]
}
```

## 2) WebSocket Endpoints

| Service | Endpoint |
|---|---|
| api-gateway | `ws://localhost:8080/ws/gateway` |
| signaling-service | `ws://localhost:8082/signal` |
| device-service | `ws://localhost:8083/ws/devices` |
| transfer-service | `ws://localhost:8085/ws/transfers` |
| config-server | `ws://localhost:8888/ws/config` |
| discovery-server | `ws://localhost:8761/ws/discovery` |

## 3) Common Envelope Rule

All websocket messages contain at least:
```json
{ "type": "MESSAGE_TYPE" }
```

Additional fields depend on message type.

---

## 4) Signaling Service Contract (`/signal`)

### REGISTER
```json
{ "type": "REGISTER", "from": "deviceA" }
```

### OFFER / ANSWER / ICE
```json
{
  "type": "OFFER",
  "from": "deviceA",
  "to": "deviceB",
  "data": "sdp-or-ice-payload"
}
```

Forwarding behavior:
- if target is connected locally -> direct websocket forward
- else if target exists on other instance -> redis relay publish

---

## 5) Device Service Contract (`/ws/devices`)

### REGISTER (client -> server)
```json
{
  "type": "REGISTER",
  "deviceId": "d1",
  "deviceName": "Pixel 8",
  "platform": "ANDROID"
}
```

### REGISTERED (server -> client)
```json
{
  "type": "REGISTERED",
  "deviceId": "d1",
  "deviceName": "Pixel 8",
  "platform": "ANDROID"
}
```

### DEVICE_JOINED / DEVICE_LEFT (broadcast)
```json
{
  "type": "DEVICE_JOINED",
  "deviceId": "d2",
  "deviceName": "MacBook",
  "platform": "MACOS"
}
```

### LIST (client -> server)
```json
{ "type": "LIST" }
```

### DEVICE_LIST (server -> client/broadcast)
```json
{
  "type": "DEVICE_LIST",
  "devices": [
    { "deviceId": "d1", "deviceName": "Pixel 8", "platform": "ANDROID" }
  ]
}
```

### HEARTBEAT
```json
{ "type": "HEARTBEAT", "deviceId": "d1" }
```

### UNREGISTER
```json
{ "type": "UNREGISTER", "deviceId": "d1" }
```

### ERROR
```json
{ "type": "ERROR" }
```

---

## 6) Transfer Service Contract (`/ws/transfers`)

### CREATE_TRANSFER
```json
{
  "type": "CREATE_TRANSFER",
  "senderDeviceId": "d1",
  "receiverDeviceId": "d2",
  "fileName": "movie.mp4",
  "fileSizeBytes": 1048576
}
```

### TRANSFER_PROGRESS
```json
{
  "type": "TRANSFER_PROGRESS",
  "transferId": "tx_123",
  "transferredBytes": 524288
}
```

### TRANSFER_COMPLETED / TRANSFER_FAILED
```json
{ "type": "TRANSFER_COMPLETED", "transferId": "tx_123" }
```

### GET_TRANSFER / LIST_TRANSFERS
```json
{ "type": "GET_TRANSFER", "transferId": "tx_123" }
```
```json
{ "type": "LIST_TRANSFERS" }
```

### Server event payload
```json
{
  "type": "TRANSFER_CREATED",
  "sessions": [
    {
      "transferId": "tx_123",
      "senderDeviceId": "d1",
      "receiverDeviceId": "d2",
      "fileName": "movie.mp4",
      "fileSizeBytes": 1048576,
      "transferredBytes": 0,
      "status": "PENDING"
    }
  ]
}
```

### Error for unsupported type
```json
{ "type": "ERROR" }
```

---

## 7) Config Service Contract (`/ws/config`)

### GET_ALL
```json
{ "type": "GET_ALL" }
```
Response:
```json
{ "type": "CONFIGS", "configs": { "k1": "v1" } }
```

### GET
```json
{ "type": "GET", "key": "feature.flag.a" }
```
Response:
```json
{ "type": "CONFIG", "key": "feature.flag.a", "value": "true" }
```

### SET
```json
{ "type": "SET", "key": "feature.flag.a", "value": "true" }
```
Response:
```json
{ "type": "CONFIG_UPDATED", "key": "feature.flag.a", "value": "true" }
```

Unsupported:
```json
{ "type": "ERROR", "value": "Unsupported config message type" }
```

---

## 8) Discovery Service Contract (`/ws/discovery`)

### REGISTER_SERVICE
```json
{
  "type": "REGISTER_SERVICE",
  "serviceName": "transfer-service",
  "host": "transfer-service",
  "port": 8085
}
```

### HEARTBEAT_SERVICE
```json
{ "type": "HEARTBEAT_SERVICE", "serviceName": "transfer-service" }
```

### UNREGISTER_SERVICE
```json
{ "type": "UNREGISTER_SERVICE", "serviceName": "transfer-service" }
```

### LIST_SERVICES
```json
{ "type": "LIST_SERVICES" }
```
Response:
```json
{
  "type": "SERVICE_LIST",
  "services": [
    { "serviceName": "transfer-service", "host": "transfer-service", "port": 8085 }
  ]
}
```

---

## 9) Gateway Service Contract (`/ws/gateway`)

### PING
Request:
```json
{ "type": "PING" }
```
Response:
```json
{ "type": "PONG", "message": "gateway-alive" }
```

### LIST_ROUTES
Request:
```json
{ "type": "LIST_ROUTES" }
```
Response:
```json
{
  "type": "ROUTES",
  "routes": [
    { "routeId": "signaling", "path": "/signal", "targetService": "signaling-service" }
  ]
}
```

Unsupported:
```json
{ "type": "ERROR", "message": "Unsupported gateway message type" }
```
