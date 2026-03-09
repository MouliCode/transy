package com.transfer.common.logging.core;

import com.transfer.common.logging.appenders.ConsoleAppender;
import com.transfer.common.logging.appenders.DatabaseAppender;
import com.transfer.common.logging.appenders.FileAppender;
import com.transfer.common.logging.filters.LevelFilter;
import com.transfer.common.logging.formatters.SimpleFormatter;

import java.sql.Connection;

/**
 * Creates logger instances from configuration properties.
 */
public final class LoggerFactory {
    private LoggerFactory() {
    }

    public static Logger createFromClasspathProperties(String loggerName) {
        return create(loggerName, LoggingProperties.fromClasspath("application.properties"), null);
    }

    public static Logger create(String loggerName, LoggingProperties properties, Connection connection) {
        LoggerImpl logger = new LoggerImpl(loggerName, false);
        logger.setLevel(properties.getRootLevel());
        logger.addFilter(new LevelFilter(properties.getFilterLevel()));
        String resolvedFilePath = properties.getFilePath().replace("%LOGGER%", loggerName);
        SimpleFormatter formatter = new SimpleFormatter(properties.getFormatPattern());

        for (String destination : properties.getDestinations()) {
            switch (destination) {
                case "console" -> {
                    ConsoleAppender appender = new ConsoleAppender(properties.getRootLevel());
                    appender.setFormatter(formatter);
                    logger.addAppender(appender);
                }
                case "file" -> {
                    FileAppender appender = new FileAppender(resolvedFilePath, properties.getRootLevel());
                    appender.setFormatter(formatter);
                    logger.addAppender(appender);
                }
                case "database" -> {
                    DatabaseAppender appender = new DatabaseAppender(properties.getDbTable(), properties.getRootLevel());
                    appender.setFormatter(formatter);
                    if (connection != null) {
                        appender.setConnection(connection);
                    }
                    logger.addAppender(appender);
                }
                default -> {
                    // Ignore unknown destination names.
                }
            }
        }

        if (logger.getAppenders().isEmpty()) {
            ConsoleAppender appender = new ConsoleAppender(properties.getRootLevel());
            appender.setFormatter(formatter);
            logger.addAppender(appender);
        }

        return logger;
    }
}
