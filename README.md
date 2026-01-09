# XA Bank Time Deposit System

A Spring Boot REST API for managing time deposit accounts with interest calculations. Built following Hexagonal Architecture (Ports & Adapters) principles, featuring PostgreSQL persistence and comprehensive API documentation via Swagger/OpenAPI.

## Table of Contents

- [Features](#features)
- [Business Rules](#business-rules)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Running Tests](#running-tests)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [License](#license)

## Features

- RESTful API for time deposit management
- Interest calculation based on plan type (Basic, Student, Premium)
- Withdrawal tracking for each deposit
- PostgreSQL database with Flyway migrations
- Swagger/OpenAPI documentation
- Testcontainers for integration testing
- Hexagonal Architecture for clean separation of concerns

## Business Rules

### Interest Calculation

| Plan Type | Annual Rate | Special Conditions |
|-----------|-------------|-------------------|
| **Basic** | 1% | Standard interest after grace period |
| **Student** | 3% | No interest after 365 days |
| **Premium** | 5% | Interest starts only after 45 days |

**Note:** All plans have a 30-day grace period where no interest is applied.

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+** (or use the included Maven wrapper)
- **Docker** and **Docker Compose** (for PostgreSQL database)

## Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/time-deposit-take-home-kata.git
   cd time-deposit-take-home-kata
   ```

2. **Start the PostgreSQL database:**
   ```bash
   docker-compose up -d
   ```
   This starts a PostgreSQL 15 instance on port 5432.

3. **Build the application:**
   ```bash
   ./mvnw clean package -DskipTests
   ```

## Running the Application

### Option 1: Using Maven
```bash
./mvnw spring-boot:run
```

### Option 2: Using the JAR file
```bash
java -jar target/time-deposit-kata-1.0-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**

## API Documentation

Once the application is running, access the Swagger UI:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

## API Endpoints

### Get All Time Deposits

Retrieves all time deposit accounts with their current balances and withdrawal history.

```http
GET /api/v1/time-deposits
```

**Response Example:**
```json
[
  {
    "id": 1,
    "planType": "basic",
    "balance": 10000.00,
    "days": 45,
    "withdrawals": [
      {
        "id": 1,
        "amount": 500.00,
        "date": "2024-01-15"
      }
    ]
  }
]
```

### Update All Balances

Applies interest calculations to all time deposits based on their plan type and age.

```http
POST /api/v1/time-deposits/update-balances
```

**Response Example:**
```json
{
  "message": "Balances updated successfully",
  "updatedCount": 3,
  "timestamp": "2024-01-08T10:30:00"
}
```

## Project Structure

```
src/
├── main/
│   ├── java/org/ikigaidigital/
│   │   ├── TimeDepositApplication.java      # Spring Boot entry point
│   │   ├── domain/
│   │   │   ├── model/                       # Domain entities & business logic
│   │   │   │   ├── TimeDeposit.java
│   │   │   │   ├── TimeDepositCalculator.java
│   │   │   │   ├── InterestStrategyFactory.java
│   │   │   │   └── strategy/                # Interest calculation strategies
│   │   │   ├── port/
│   │   │   │   ├── input/                   # Use case interfaces
│   │   │   │   └── output/                  # Repository interfaces
│   │   │   └── service/                     # Domain service implementations
│   │   ├── infrastructure/
│   │   │   ├── adapter/
│   │   │   │   ├── input/rest/              # REST controllers & DTOs
│   │   │   │   └── output/persistence/      # JPA entities & repositories
│   │   │   └── config/                      # Spring configurations
│   │   └── shared/
│   │       └── mapper/                      # Object mappers
│   └── resources/
│       ├── application.yml                  # Main configuration
│       ├── application-local.yml            # Local development settings
│       └── db/migration/                    # Flyway SQL migrations
└── test/
    └── java/org/ikigaidigital/
        ├── integration/                     # Integration tests (Testcontainers)
        ├── domain/                          # Domain unit tests
        └── shared/                          # Mapper tests
```

## Running Tests

### Run All Tests
```bash
./mvnw test
```

### Run Unit Tests Only
```bash
./mvnw test -Dtest="!*IntegrationTest"
```

### Run Integration Tests Only
```bash
./mvnw test -Dtest="*IntegrationTest"
```

> **Note:** Integration tests use Testcontainers and require Docker to be running.

### Run Specific Test Class
```bash
./mvnw test -Dtest=TimeDepositCalculatorTest
```

## Configuration

### Application Profiles

| Profile | Description | Database |
|---------|-------------|----------|
| `local` (default) | Local development | PostgreSQL via docker-compose |
| `test` | Integration testing | Testcontainers PostgreSQL |
| `docker` | Docker deployment | External PostgreSQL |

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `local` | Active Spring profile |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/timedeposit` | Database URL |
| `SPRING_DATASOURCE_USERNAME` | `timedeposit` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `timedeposit123` | Database password |
| `SERVER_PORT` | `8080` | Application server port |

## Database Schema

### time_deposits

| Column | Type | Description |
|--------|------|-------------|
| `id` | SERIAL | Primary key |
| `plan_type` | VARCHAR(50) | Plan type: basic, student, premium |
| `balance` | DECIMAL(19,2) | Current balance |
| `days` | INTEGER | Days since deposit creation |
| `created_at` | TIMESTAMP | Record creation timestamp |
| `updated_at` | TIMESTAMP | Last update timestamp |

### withdrawals

| Column | Type | Description |
|--------|------|-------------|
| `id` | SERIAL | Primary key |
| `time_deposit_id` | INTEGER | Foreign key to time_deposits |
| `amount` | DECIMAL(19,2) | Withdrawal amount |
| `withdrawal_date` | DATE | Date of withdrawal |
| `created_at` | TIMESTAMP | Record creation timestamp |

## Docker Compose

### Running the Full Stack

Run both the database and application together with a single command:

```bash
# Build and start all services
docker-compose up -d --build

# View combined logs
docker-compose logs -f

# View application logs only
docker-compose logs -f app

# Check service status
docker-compose ps
```

Once started, access:
- **Application API:** http://localhost:8080/api/v1/time-deposits
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Health Check:** http://localhost:8080/actuator/health

### Running Database Only (Local Development)

For local development with the application running outside Docker:

```bash
# Start only the PostgreSQL database
docker-compose up -d postgres

# Run the application locally
./mvnw spring-boot:run
```

### Docker Compose Commands

```bash
# Build images without starting
docker-compose build

# Start services in background
docker-compose up -d

# Stop services
docker-compose stop

# Stop and remove containers
docker-compose down

# Stop and remove containers with volumes (deletes data)
docker-compose down -v

# Rebuild and restart the application
docker-compose up -d --build app
```

### Services

| Service | Container Name | Port | Description |
|---------|---------------|------|-------------|
| `postgres` | timedeposit-db | 5432 | PostgreSQL 15 database |
| `app` | timedeposit-app | 8080 | Spring Boot application |

### Database Connection Details

| Property | Local Development | Docker Network |
|----------|------------------|----------------|
| Host | localhost | postgres |
| Port | 5432 | 5432 |
| Database | timedeposit | timedeposit |
| Username | timedeposit | timedeposit |
| Password | timedeposit123 | timedeposit123 |

## Health Check

The application exposes health endpoints via Spring Actuator:

```bash
# Health check
curl http://localhost:8080/actuator/health

# Application info
curl http://localhost:8080/actuator/info
```

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Data JPA**
- **PostgreSQL 15**
- **Flyway** (Database migrations)
- **SpringDoc OpenAPI** (Swagger documentation)
- **JUnit 5** (Testing)
- **Testcontainers** (Integration testing)
- **AssertJ** (Fluent assertions)

## Architecture

This project follows **Hexagonal Architecture** (Ports & Adapters):

```
┌─────────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                      │
│  ┌─────────────────┐                    ┌─────────────────┐ │
│  │  REST Adapter   │                    │  JPA Adapter    │ │
│  │  (Controllers)  │                    │  (Repositories) │ │
│  └────────┬────────┘                    └────────┬────────┘ │
│           │                                       │          │
│           ▼                                       ▼          │
│  ┌─────────────────┐                    ┌─────────────────┐ │
│  │   Input Port    │                    │  Output Port    │ │
│  │   (Service IF)  │                    │  (Repository IF)│ │
│  └────────┬────────┘                    └────────┬────────┘ │
│           │                                       │          │
│           └───────────────┬───────────────────────┘          │
│                           ▼                                   │
│                 ┌─────────────────┐                          │
│                 │  Domain Layer   │                          │
│                 │  (Business      │                          │
│                 │   Logic)        │                          │
│                 └─────────────────┘                          │
└─────────────────────────────────────────────────────────────┘
```

## Security Considerations

This section documents security measures implemented and considerations for production deployment.

### Implemented Security Measures

| Measure | Description |
|---------|-------------|
| **Stack Trace Suppression** | Server configured to never expose stack traces in API responses |
| **Global Exception Handler** | All exceptions return sanitized error messages, full details logged server-side |
| **Actuator Endpoint Lockdown** | Only `/actuator/health` and `/actuator/info` exposed publicly |
| **API Versioning** | Endpoints versioned (`/api/v1/`) for future backward compatibility |
| **Parameterized Queries** | JPA/Hibernate prevents SQL injection by default |
| **Input Validation Ready** | `spring-boot-starter-validation` dependency included for Bean Validation |

### Production Deployment Recommendations

For a production fintech/banking deployment, the following additional measures should be implemented:

#### Authentication & Authorization
- **OAuth2/JWT Authentication**: Secure API endpoints with token-based authentication
- **Role-Based Access Control (RBAC)**: Implement roles for different access levels
- **API Key Management**: For service-to-service communication

#### Network Security
- **HTTPS Enforcement**: TLS 1.3 for all communications
- **CORS Configuration**: Restrict allowed origins to known clients
- **Rate Limiting**: Prevent DoS attacks and API abuse (e.g., Bucket4j)
- **WAF Integration**: Web Application Firewall for additional protection

#### Data Security
- **Encryption at Rest**: Encrypt sensitive financial data in database
- **Encryption in Transit**: Already covered by HTTPS
- **Data Masking**: Mask account numbers and balances in logs
- **Audit Logging**: Log all financial transactions with user context

#### Infrastructure Security
- **Secrets Management**: Use HashiCorp Vault or AWS Secrets Manager for credentials
- **Container Security**: Run as non-root user (already configured in Dockerfile)
- **Dependency Scanning**: OWASP Dependency-Check in CI/CD pipeline
- **Static Analysis**: SpotBugs with FindSecBugs for security bug detection

#### Compliance Considerations
- **PCI DSS**: If handling card data
- **GDPR**: For EU customer data
- **SOC 2**: For financial services
- **Audit Trail**: Immutable logging of all data changes

### Security Anti-Patterns Avoided

| Anti-Pattern | How Avoided |
|--------------|-------------|
| Stack trace exposure | Global exception handler + server config |
| SQL injection | JPA parameterized queries |
| Sensitive data in logs | Appropriate log levels configured |
| Overly permissive actuator | Restricted to health/info only |
| Hardcoded secrets | Environment variables for credentials |

## License

This project is part of a take-home coding kata assignment.

---

## Quick Start Summary

```bash
# 1. Run application with Docker
docker-compose up -d

# 2. Run application directly
./mvnw spring-boot:run

# 3. Open Swagger UI
open http://localhost:8080/swagger-ui.html

# 4. Try the API
curl http://localhost:8080/api/v1/time-deposits
```

