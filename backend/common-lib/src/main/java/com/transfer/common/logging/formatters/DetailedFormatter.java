package com.transfer.common.logging.formatters;

import com.transfer.common.logging.core.LogFormatter;
import com.transfer.common.logging.core.LogMessage;

/**
 * Formatter for detailed logs including source.
 */
public class DetailedFormatter implements LogFormatter {
    private String pattern;
    private String dateFormat;

    public DetailedFormatter() {
        this("[%LEVEL] %TIMESTAMP [%SOURCE] - %MESSAGE");
    }

    public DetailedFormatter(String pattern) {
        this.pattern = pattern;
        this.dateFormat = "yyyy-MM-dd HH:mm:ss";
    }

    @Override
    public String format(LogMessage message) {
        String source = message.getSource() == null ? "Unknown" : message.getSource();
        if (pattern == null || pattern.isEmpty()) {
            return "[" + message.getLevel() + "] " + message.getTimestamp() + " [" + source + "] - " + message.getMessage();
        }
        return pattern
                .replace("%LEVEL", message.getLevel().name())
                .replace("%TIMESTAMP", message.getTimestamp().toString())
                .replace("%MESSAGE", message.getMessage())
                .replace("%SOURCE", source);
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
