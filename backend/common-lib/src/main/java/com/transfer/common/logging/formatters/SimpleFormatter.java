package com.transfer.common.logging.formatters;

import com.transfer.common.logging.core.LogFormatter;
import com.transfer.common.logging.core.LogMessage;

/**
 * Formatter for compact text logs.
 */
public class SimpleFormatter implements LogFormatter {
    private String pattern;
    private String dateFormat;

    public SimpleFormatter() {
        this("[%LEVEL] %TIMESTAMP - %MESSAGE");
    }

    public SimpleFormatter(String pattern) {
        this.pattern = pattern;
        this.dateFormat = "yyyy-MM-dd HH:mm:ss";
    }

    @Override
    public String format(LogMessage message) {
        if (pattern == null || pattern.isEmpty()) {
            return "[" + message.getLevel() + "] " + message.getTimestamp() + " - " + message.getMessage();
        }

        return pattern
                .replace("%LEVEL", message.getLevel().name())
                .replace("%TIMESTAMP", message.getTimestamp().toString())
                .replace("%MESSAGE", message.getMessage())
                .replace("%SOURCE", message.getSource() == null ? "" : message.getSource());
    }

    @Override
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }
}
