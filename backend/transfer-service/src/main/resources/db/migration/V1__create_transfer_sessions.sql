CREATE TABLE IF NOT EXISTS transfer_sessions (
    transfer_id VARCHAR(64) PRIMARY KEY,
    sender_device_id VARCHAR(128) NOT NULL,
    receiver_device_id VARCHAR(128) NOT NULL,
    file_name VARCHAR(512) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    transferred_bytes BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_transfer_sessions_sender ON transfer_sessions(sender_device_id);
CREATE INDEX IF NOT EXISTS idx_transfer_sessions_receiver ON transfer_sessions(receiver_device_id);
CREATE INDEX IF NOT EXISTS idx_transfer_sessions_status ON transfer_sessions(status);
