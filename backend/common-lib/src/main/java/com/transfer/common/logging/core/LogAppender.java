package com.transfer.common.logging.core;

public interface LogAppender {
    void append(LogMessage message);

    void setLevel(LogLevel level);

    LogLevel getLevel();

    boolean isEnabled(LogLevel level);

    void setFormatter(LogFormatter formatter);

    LogFormatter getFormatter();
}
