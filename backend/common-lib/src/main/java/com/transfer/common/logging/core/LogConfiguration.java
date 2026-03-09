package com.transfer.common.logging.core;

/**
 * Global logger configuration.
 */
public class LogConfiguration {
    private LogLevel rootLevel = LogLevel.INFO;

    public LogConfiguration() {
    }

    public LogConfiguration(LogLevel rootLevel) {
        this.rootLevel = rootLevel;
    }

    public LogLevel getRootLevel() {
        return rootLevel;
    }

    public void setRootLevel(LogLevel rootLevel) {
        this.rootLevel = rootLevel;
    }
}
