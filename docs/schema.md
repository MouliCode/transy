# Backend Schema

## 1) PostgreSQL Schema

### Table: `transfer_sessions`
Purpose: durable metadata for file transfer lifecycle.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `transfer_id` | `VARCHAR(64)` | `PRIMARY KEY` | transfer identifier |
| `sender_device_id` | `VARCHAR(128)` | `NOT NULL` | sender device id |
| `receiver_device_id` | `VARCHAR(128)` | `NOT NULL` | receiver device id |
| `file_name` | `VARCHAR(512)` | `NOT NULL` | original file name |
| `file_size_bytes` | `BIGINT` | `NOT NULL` | file size in bytes |
| `transferred_bytes` | `BIGINT` | `NOT NULL DEFAULT 0` | progress bytes |
| `status` | `VARCHAR(32)` | `NOT NULL` | `PENDING/IN_PROGRESS/COMPLETED/FAILED` |
| `created_at` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL` | create timestamp |
| `updated_at` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL` | update timestamp |

Indexes:
- `idx_transfer_sessions_sender(sender_device_id)`
- `idx_transfer_sessions_receiver(receiver_device_id)`
- `idx_transfer_sessions_status(status)`

## 2) Redis Keyspace

### `signaling:session-owner` (Hash)
- key: `deviceId`
- value: `instanceId`
- used by signaling-service for cross-instance ownership resolution

### `device:session-owner` (Hash)
- key: `deviceId`
- value: `instanceId`
- used by device-service for session ownership

### `device:registry` (Hash)
- key: `deviceId`
- value: serialized `Device` json
- used by device-service primary repository

### `signaling:relay` (Pub/Sub channel)
- message payload: relay envelope containing `toDeviceId` + signaling payload

## 3) Logging (optional DB destination)

If `database` destination is enabled in custom logger, expected table shape:

### Table: `application_logs`

| Column | Type | Description |
|---|---|---|
| `timestamp` | `TIMESTAMP WITH TIME ZONE` | event time |
| `level` | `VARCHAR(16)` | DEBUG/INFO/WARNING/ERROR/FATAL |
| `message` | `TEXT` | formatted message text |
| `source` | `VARCHAR(512)` | source class/method |

Note: this table is not auto-migrated by default. Create it when enabling DB appender.
