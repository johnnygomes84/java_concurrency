# Java 25 Concurrency Demo

Demonstrates **Structured Concurrency (JEP 505)** and **Scoped Values (JEP 506)**.

## Requirements
- JDK 25

## Run
```bash
./gradlew bootRun
```

## Endpoints

- `GET /java25/demo` - Full demo with all features
- `GET /java25/scoped-values` - Scoped values demo

## JVM Flags
Preview features enabled in `build.gradle`:
```
--enable-preview
```

