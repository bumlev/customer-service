# Customer Service

A Spring Boot microservice for managing customer information with RESTful APIs.

## Overview

This is a customer management service built with Spring Boot 3.5.5 that provides CRUD operations for customer data. The service is designed to work within a microservices architecture, registering with Eureka service discovery.

## Features

- RESTful API for customer management
- CRUD operations (Create, Read, Update)
- H2 in-memory database
- Eureka service discovery integration
- OpenAPI/Swagger documentation
- JaCoCo code coverage reporting (80% line coverage, 70% branch coverage required)
- Docker support

## Tech Stack

- Java 17
- Spring Boot 3.5.5
- Spring Cloud 2025.0.0
- Spring Data JPA
- H2 Database
- Lombok
- SpringDoc OpenAPI 2.8.5
- JaCoCo for code coverage
- Maven

## Prerequisites

- JDK 17 or higher
- Maven 3.6+
- Docker (optional, for containerized deployment)

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/bumlev/customer-service.git
cd customer-service
```

### Build the Project

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on port `8091`.

### Run Tests

```bash
mvn test
```

### Generate Code Coverage Report

```bash
mvn jacoco:report
```

The coverage report will be generated in `target/site/jacoco/index.html`.

### Verify Code Coverage

```bash
mvn clean verify
```

This will run tests and check that code coverage meets the configured thresholds (80% line coverage, 70% branch coverage).

## API Endpoints

### Base URL
```
http://localhost:8091
```

### Endpoints

#### Get All Customers
```http
GET /customers
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "1234567890"
  }
]
```

#### Create Customer
```http
POST /customers
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890"
}
```

#### Get Customer by ID
```http
GET /customers/{id}
```

**Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890"
}
```

#### Update Customer
```http
PUT /customers/{id}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Jane Doe",
  "email": "jane.doe@example.com",
  "phone": "0987654321"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Jane Doe",
  "email": "jane.doe@example.com",
  "phone": "0987654321"
}
```

## API Documentation

### Swagger UI
Once the application is running, access the Swagger UI at:
```
http://localhost:8091/swagger-ui-custom.html
```

### OpenAPI Specification
Access the OpenAPI JSON specification at:
```
http://localhost:8091/api-docs
```

## Database

The application uses an H2 in-memory database for development and testing.

### H2 Console
Access the H2 database console at:
```
http://localhost:8091/h2-console
```

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:customerdb`
- Username: `H2`
- Password: `admin`

## Docker Deployment

### Build Docker Image

```bash
docker build -t customer-service:latest .
```

### Run Docker Container

```bash
docker run -p 8091:8091 customer-service:latest
```

### Multi-Stage Build

The Dockerfile uses a multi-stage build to optimize the image size:
1. Stage 1: Maven build stage using `maven:3.8.3-openjdk-17`
2. Stage 2: Runtime stage using `openjdk:17-jdk`

## Configuration

The application configuration is located in `src/main/resources/application.yml`.

### Key Configuration Properties

| Property | Value | Description |
|----------|-------|-------------|
| `server.port` | 8091 | Application server port |
| `spring.application.name` | customer-service | Service name |
| `spring.datasource.url` | jdbc:h2:mem:customerdb | Database connection URL |
| `eureka.client.service-url.defaultZone` | http://localhost:8761/eureka | Eureka server URL |

## Service Discovery

This service registers with Eureka service discovery. Ensure an Eureka server is running at `http://localhost:8761` for service registration to work properly.

To disable Eureka registration for local development, you can set:
```yaml
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

## Code Coverage Requirements

The project enforces the following code coverage requirements:
- **Line Coverage**: Minimum 80%
- **Branch Coverage**: Minimum 70%
- **Method Coverage**: Maximum 1 missed method per class (excludes entities and DTOs)

Coverage checks are automatically run during the `verify` phase.

## CI/CD

The project includes a GitHub Actions workflow (`.github/workflows/testing-pipeline.yml`) that:
1. Builds the project
2. Runs tests
3. Generates JaCoCo coverage reports
4. Uploads coverage artifacts

## Project Structure

```
customer-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/service/customerservice/
│   │   │       ├── controller/       # REST controllers
│   │   │       ├── dto/              # Data Transfer Objects
│   │   │       ├── entity/           # JPA entities
│   │   │       ├── exception/        # Custom exceptions
│   │   │       ├── repository/       # JPA repositories
│   │   │       ├── service/          # Business logic
│   │   │       ├── util/             # Utility classes
│   │   │       └── CustomerServiceApplication.java
│   │   └── resources/
│   │       └── application.yml       # Configuration
│   └── test/
│       └── java/                     # Test classes
├── Dockerfile
├── pom.xml
└── README.md
```

## Development

### Running Locally Without Eureka

If you want to run the service without Eureka, modify `application.yml`:

```yaml
eureka:
  client:
    enabled: false
```

Or use Spring profiles to manage different environments.

## Troubleshooting

### Port Already in Use
If port 8091 is already in use, you can change it in `application.yml`:
```yaml
server:
  port: 8092
```

### Eureka Connection Issues
If Eureka server is not available, the application will still start but will log connection errors. This is normal for local development without a service registry.

## License

This project is licensed under the terms specified in the `pom.xml` file.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Ensure tests pass and coverage requirements are met
5. Submit a pull request

## Contact

For more information, please refer to the developer information in `pom.xml`.
