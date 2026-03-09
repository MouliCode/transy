package com.transfer.common.logging.core;

public interface LogFilter {
    boolean shouldLog(LogMessage message);

    void setLevel(LogLevel level);

    LogLevel getLevel();
}
