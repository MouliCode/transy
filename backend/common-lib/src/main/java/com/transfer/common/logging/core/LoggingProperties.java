package com.transfer.common.logging.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Property-backed configuration for the custom logger framework.
 */
public class LoggingProperties {
    public static final String ROOT_LEVEL_KEY = "app.logging.root-level";
    public static final String DESTINATIONS_KEY = "app.logging.destinations";
    public static final String FILE_PATH_KEY = "app.logging.file.path";
    public static final String DB_TABLE_KEY = "app.logging.db.table";

    private final LogLevel rootLevel;
    private final List<String> destinations;
    private final String filePath;
    private final String dbTable;

    private LoggingProperties(LogLevel rootLevel, List<String> destinations, String filePath, String dbTable) {
        this.rootLevel = rootLevel;
        this.destinations = destinations;
        this.filePath = filePath;
        this.dbTable = dbTable;
    }

    public static LoggingProperties fromProperties(Properties properties) {
        String rootLevelRaw = properties.getProperty(ROOT_LEVEL_KEY, "2").trim();
        LogLevel rootLevel;
        if (rootLevelRaw.matches("\\d+")) {
            rootLevel = LogLevel.fromNumeric(Integer.parseInt(rootLevelRaw));
        } else {
            rootLevel = LogLevel.fromText(rootLevelRaw);
        }

        String destinationsRaw = properties.getProperty(DESTINATIONS_KEY, "console");
        List<String> destinations = new ArrayList<>();
        for (String destination : destinationsRaw.split(",")) {
            String normalized = destination.trim().toLowerCase();
            if (!normalized.isEmpty()) {
                destinations.add(normalized);
            }
        }

        String filePath = properties.getProperty(FILE_PATH_KEY, "application.log");
        String dbTable = properties.getProperty(DB_TABLE_KEY, "application_logs");
        return new LoggingProperties(rootLevel, destinations, filePath, dbTable);
    }

    public static LoggingProperties fromClasspath(String resourceName) {
        Properties properties = new Properties();
        try (InputStream inputStream = LoggingProperties.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load logging properties from " + resourceName, e);
        }
        return fromProperties(properties);
    }

    public LogLevel getRootLevel() {
        return rootLevel;
    }

    public List<String> getDestinations() {
        return destinations;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getDbTable() {
        return dbTable;
    }
}
