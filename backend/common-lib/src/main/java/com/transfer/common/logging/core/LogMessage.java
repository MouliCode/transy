package com.transfer.common.logging.core;

import java.time.Instant;

/**
 * Immutable log message model.
 */
public class LogMessage {
    private final Instant timestamp;
    private final LogLevel level;
    private final String message;
    private final String source;

    private LogMessage(Builder builder) {
        this.timestamp = builder.timestamp;
        this.level = builder.level;
        this.message = builder.message;
        this.source = builder.source;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getSource() {
        return source;
    }

    public static class Builder {
        private Instant timestamp = Instant.now();
        private LogLevel level;
        private String message;
        private String source;

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder level(LogLevel level) {
            this.level = level;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public LogMessage build() {
            if (level == null) {
                throw new IllegalStateException("LogLevel is required");
            }
            if (message == null || message.trim().isEmpty()) {
                throw new IllegalStateException("Message is required");
            }
            return new LogMessage(this);
        }
    }
}
