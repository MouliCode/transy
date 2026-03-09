package com.transfer.common.logging.core;

import com.transfer.common.logging.appenders.ConsoleAppender;
import com.transfer.common.logging.appenders.DatabaseAppender;
import com.transfer.common.logging.appenders.FileAppender;

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

        for (String destination : properties.getDestinations()) {
            switch (destination) {
                case "console" -> {
                    ConsoleAppender appender = new ConsoleAppender(properties.getRootLevel());
                    logger.addAppender(appender);
                }
                case "file" -> {
                    FileAppender appender = new FileAppender(properties.getFilePath(), properties.getRootLevel());
                    logger.addAppender(appender);
                }
                case "database" -> {
                    DatabaseAppender appender = new DatabaseAppender(properties.getDbTable(), properties.getRootLevel());
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
            logger.addAppender(new ConsoleAppender(properties.getRootLevel()));
        }

        return logger;
    }
}
