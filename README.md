# Rest Assured + Cucumber API Test Project

This project demonstrates BDD-style REST API testing using **Rest Assured** and **Cucumber** with **Maven**.

## Tech Stack

- Java 17
- Maven
- Rest Assured
- Cucumber (JUnit Platform)

## Project Structure

- `src/test/resources/features` - Cucumber feature files
- `src/test/java/com/example/apitests/steps` - Step definitions and hooks
- `src/test/java/com/example/apitests/runner` - Test runner
- `src/test/resources/config.properties` - Environment configuration

## Run Tests

```bash
mvn test
```

## Override Base URL

Default base URL is defined in `src/test/resources/config.properties`.
You can override it at runtime:

```bash
mvn test -DbaseUrl=https://your-api-base-url
```
