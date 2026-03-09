package com.transfer.common.logging.filters;

import com.transfer.common.logging.core.LogFilter;
import com.transfer.common.logging.core.LogLevel;
import com.transfer.common.logging.core.LogMessage;

/**
 * Filter that allows messages whose level is >= configured level.
 */
public class LevelFilter implements LogFilter {
    private LogLevel level;

    public LevelFilter() {
        this(LogLevel.DEBUG);
    }

    public LevelFilter(LogLevel level) {
        this.level = level;
    }

    @Override
    public boolean shouldLog(LogMessage message) {
        return message.getLevel().isGreaterOrEqual(level);
    }

    @Override
    public void setLevel(LogLevel level) {
        this.level = level;
    }

    @Override
    public LogLevel getLevel() {
        return level;
    }
}
