package com.transfer.common.logging.main;

import com.transfer.common.logging.core.Logger;
import com.transfer.common.logging.core.LoggerFactory;

/**
 * Demonstrates custom logger usage with app.logging.root-level from application.properties.
 */
public class LoggingDemo {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.createFromClasspathProperties("LoggingDemo");

        logger.debug("Debug message");
        logger.info("Info message");
        logger.warning("Warning message");
        logger.error("Error message");
        logger.fatal("Fatal message");
    }
}
