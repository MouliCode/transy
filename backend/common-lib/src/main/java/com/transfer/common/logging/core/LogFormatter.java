package com.transfer.common.logging.core;

public interface LogFormatter {
    String format(LogMessage message);

    void setPattern(String pattern);

    String getPattern();

    void setDateFormat(String dateFormat);
}
