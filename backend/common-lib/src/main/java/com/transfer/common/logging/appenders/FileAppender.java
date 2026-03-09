package com.transfer.common.logging.appenders;

import com.transfer.common.logging.core.LogAppender;
import com.transfer.common.logging.core.LogFormatter;
import com.transfer.common.logging.core.LogLevel;
import com.transfer.common.logging.core.LogMessage;
import com.transfer.common.logging.formatters.SimpleFormatter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Appender that writes log messages to a file.
 */
public class FileAppender implements LogAppender {
    private LogLevel level;
    private LogFormatter formatter;
    private final String filePath;
    private PrintWriter writer;

    public FileAppender(String filePath) {
        this(filePath, LogLevel.DEBUG);
    }

    public FileAppender(String filePath, LogLevel level) {
        this.filePath = filePath;
        this.level = level;
        this.formatter = new SimpleFormatter();
        initializeWriter();
    }

    private void initializeWriter() {
        try {
            Path path = Path.of(filePath);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            this.writer = new PrintWriter(
                    Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND),
                    true
            );
        } catch (IOException e) {
            System.err.println("Failed to initialize FileAppender for " + filePath + ": " + e.getMessage());
        }
    }

    @Override
    public void append(LogMessage message) {
        if (!isEnabled(message.getLevel()) || writer == null) {
            return;
        }

        try {
            writer.println(formatter.format(message));
            writer.flush();
        } catch (Exception e) {
            System.err.println("Failed to write to file " + filePath + ": " + e.getMessage());
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

    public String getFilePath() {
        return filePath;
    }

    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
}
