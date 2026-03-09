package com.transfer.common.logging.core;

/**
 * Represents log severity. Numeric mapping supports configuration via properties.
 */
public enum LogLevel {
    DEBUG(1),
    INFO(2),
    WARNING(3),
    ERROR(4),
    FATAL(5);

    private final int priority;

    LogLevel(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isGreaterOrEqual(LogLevel other) {
        return this.priority >= other.priority;
    }

    public static LogLevel fromNumeric(int value) {
        for (LogLevel level : values()) {
            if (level.priority == value) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unsupported log level number: " + value);
    }

    public static LogLevel fromText(String value) {
        return LogLevel.valueOf(value.trim().toUpperCase());
    }
}
