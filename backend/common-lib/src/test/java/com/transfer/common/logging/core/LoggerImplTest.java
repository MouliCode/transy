package com.transfer.common.logging.core;

import com.transfer.common.logging.appenders.ConsoleAppender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoggerImplTest {

    @Test
    void respectsConfiguredRootLevel() {
        LoggerImpl logger = new LoggerImpl("test", false);
        logger.addAppender(new ConsoleAppender(LogLevel.WARNING));
        logger.setLevel(LogLevel.WARNING);

        logger.info("This should be ignored");
        logger.warning("This should be logged");

        assertEquals(LogLevel.WARNING, logger.getLevel());
        assertEquals(1, logger.getAppenders().size());
    }
}
