package com.transfer.common.logging.appenders;

import com.transfer.common.logging.core.LogAppender;
import com.transfer.common.logging.core.LogFormatter;
import com.transfer.common.logging.core.LogLevel;
import com.transfer.common.logging.core.LogMessage;
import com.transfer.common.logging.formatters.SimpleFormatter;

import java.io.PrintStream;

/**
 * Appender that writes log messages to console streams.
 */
public class ConsoleAppender implements LogAppender {
    private LogLevel level;
    private LogFormatter formatter;
    private PrintStream outputStream;

    public ConsoleAppender() {
        this(LogLevel.DEBUG);
    }

    public ConsoleAppender(LogLevel level) {
        this.level = level;
        this.formatter = new SimpleFormatter();
        this.outputStream = System.out;
    }

    @Override
    public void append(LogMessage message) {
        if (!isEnabled(message.getLevel())) {
            return;
        }

        String formatted = formatter.format(message);
        if (message.getLevel() == LogLevel.ERROR || message.getLevel() == LogLevel.FATAL) {
            System.err.println(formatted);
        } else {
            outputStream.println(formatted);
        }
    }

    @Override
    public void setLevel(LogLevel level) {
        this.level = level;
    }

    @Override
    public LogLevel getLevel() {
        return level;
    }

    @Override
    public boolean isEnabled(LogLevel level) {
        return level.isGreaterOrEqual(this.level);
    }

    @Override
    public void setFormatter(LogFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public LogFormatter getFormatter() {
        return formatter;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}
