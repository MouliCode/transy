# Custom Logging Framework

## 1. Overview
Custom logger framework is implemented in:
- `backend/common-lib/src/main/java/com/transfer/common/logging`

It supports:
- configurable root log level
- configurable destination list
- filters
- formatter pattern
- console/file/database appenders

## 2. Centralized Configuration
All services read the same logger settings from one shared file:
- `backend/common-lib/src/main/resources/application.properties`

## 3. Supported Properties
- `app.logging.root-level`
  - numeric: `1=DEBUG, 2=INFO, 3=WARNING, 4=ERROR, 5=FATAL`
- `app.logging.destinations`
  - comma-separated: `console,file,database`
- `app.logging.file.path`
  - supports `%LOGGER%` placeholder
- `app.logging.db.table`
- `app.logging.filter.level`
  - same numeric/text mapping as root-level
- `app.logging.format.pattern`
  - tokens: `%LEVEL`, `%TIMESTAMP`, `%SOURCE`, `%MESSAGE`

## 4. Default Production Behavior
Recommended:
- `app.logging.root-level=3`
- `app.logging.filter.level=3`

This prints only:
- `WARNING`
- `ERROR`
- `FATAL`

## 5. Runtime Usage in Classes
Handlers/services instantiate logger via:
- `com.transfer.common.logging.core.LoggerFactory.createFromClasspathProperties(<LoggerName>)`

Example:

```java
private final Logger logger = LoggerFactory.createFromClasspathProperties(getClass().getSimpleName());
```

Then use:
- `logger.info(...)`
- `logger.warning(...)`
- `logger.error(...)`

## 6. Destinations
- ConsoleAppender:
  - INFO/WARN to stdout
  - ERROR/FATAL to stderr
- FileAppender:
  - appends to configured file path
- DatabaseAppender:
  - inserts into configured table (if connection is provided)
