# Backend - TPX URL Shortener

This is the backend of the TPX URL Shortener project, built with Java and Spring Boot.

## Technologies Used

- **Java 21+**: Programming language.
- **Spring Boot**: Framework for building the backend service.
- **Maven**: Build automation tool.
- **JUnit**: For unit testing.

## Getting Started

### Prerequisites

- Java 17 or later installed.
- Maven installed.

### Development Setup

1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. The backend API (with swagger UI) will be available at [http://localhost:8080](http://localhost:8080/swagger-ui/index.html).

## Features

This API implements the openapi document described [here](../openapi.yaml). It includes the following features:

✅ Storage and retrieval of aliased URLs using JPA.
✅ Input validation - see [this class](./src/main/java/com/tpximpact/shortenerservice/service/ShortenRequestValidationService.java) for details
✅ Alias generation.
✅ Automatic Redirection.

## Running Tests

To run tests, use the following command:
```bash
mvn test
```

## Learn More

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Maven Documentation](https://maven.apache.org/)