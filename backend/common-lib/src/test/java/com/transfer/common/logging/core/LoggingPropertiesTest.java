package com.transfer.common.logging.core;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoggingPropertiesTest {

    @Test
    void mapsNumericRootLevelFromProperties() {
        Properties properties = new Properties();
        properties.setProperty("app.logging.root-level", "3");
        properties.setProperty("app.logging.destinations", "console,file");

        LoggingProperties loggingProperties = LoggingProperties.fromProperties(properties);

        assertEquals(LogLevel.WARNING, loggingProperties.getRootLevel());
        assertEquals(2, loggingProperties.getDestinations().size());
    }

    @Test
    void mapsTextRootLevelFromProperties() {
        Properties properties = new Properties();
        properties.setProperty("app.logging.root-level", "error");

        LoggingProperties loggingProperties = LoggingProperties.fromProperties(properties);

        assertEquals(LogLevel.ERROR, loggingProperties.getRootLevel());
    }
}
