package com.transfer.common.logging.core;

import java.util.List;

public interface Logger {
    void debug(String message);

    void info(String message);

    void warning(String message);

    void error(String message);

    void fatal(String message);

    void log(LogLevel level, String message);

    void setLevel(LogLevel level);

    void addAppender(LogAppender appender);

    void addFilter(LogFilter filter);

    void removeFilter(LogFilter filter);

    List<LogAppender> getAppenders();

    List<LogFilter> getFilters();
}
