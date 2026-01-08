# Time Deposit System - Comprehensive Implementation Plan

## Executive Summary

This document outlines the implementation plan for completing the XA Bank Time Deposit system. The existing codebase contains partial domain logic (`TimeDeposit` class and `TimeDepositCalculator`) that must be preserved while adding RESTful API capabilities, database persistence, and proper architecture.

---

## 1. Architecture Overview

### 1.1 Hexagonal Architecture (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────────────────┐
│                          INFRASTRUCTURE                              │
│  ┌─────────────────┐                        ┌─────────────────────┐ │
│  │   REST API      │                        │   PostgreSQL        │ │
│  │   (Controller)  │                        │   (Repository)      │ │
│  │   Primary       │                        │   Secondary         │ │
│  │   Adapter       │                        │   Adapter           │ │
│  └────────┬────────┘                        └──────────┬──────────┘ │
│           │                                            │            │
│  ┌────────▼────────┐                        ┌──────────▼──────────┐ │
│  │   Input Port    │                        │   Output Port       │ │
│  │   (API Service) │                        │   (Repository IF)   │ │
│  └────────┬────────┘                        └──────────▲──────────┘ │
│           │                                            │            │
│  ┌────────┴────────────────────────────────────────────┴──────────┐ │
│  │                      DOMAIN CORE                                │ │
│  │  ┌──────────────────────────────────────────────────────────┐  │ │
│  │  │  TimeDeposit (Entity)  │  TimeDepositCalculator (Service)│  │ │
│  │  │  Withdrawal (Entity)   │  Interest Strategies            │  │ │
│  │  └──────────────────────────────────────────────────────────┘  │ │
│  └─────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 Project Package Structure

```
src/main/java/org/ikigaidigital/
├── TimeDeposit.java                    # Existing - DO NOT MODIFY
├── TimeDepositCalculator.java          # Existing - DO NOT MODIFY signature
├── TimeDepositApplication.java         # Spring Boot main class
│
├── domain/
│   ├── model/
│   │   ├── TimeDepositEntity.java      # JPA Entity for DB
│   │   └── WithdrawalEntity.java       # JPA Entity for withdrawals
│   ├── port/
│   │   ├── input/
│   │   │   └── TimeDepositService.java # Input port interface
│   │   └── output/
│   │       ├── TimeDepositRepository.java
│   │       └── WithdrawalRepository.java
│   └── service/
│       └── TimeDepositServiceImpl.java # Core business logic
│
├── infrastructure/
│   ├── adapter/
│   │   ├── input/
│   │   │   └── rest/
│   │   │       ├── TimeDepositController.java
│   │   │       └── dto/
│   │   │           └── TimeDepositResponseDTO.java
│   │   └── output/
│   │       └── persistence/
│   │           ├── JpaTimeDepositRepository.java
│   │           └── JpaWithdrawalRepository.java
│   └── config/
│       ├── OpenApiConfig.java
│       └── SecurityConfig.java
│
└── shared/
    └── mapper/
        └── TimeDepositMapper.java
```

---

## 2. Database Schema Design

### 2.1 Entity Relationship Diagram

```
┌─────────────────────────────────┐       ┌─────────────────────────────────┐
│         time_deposits           │       │          withdrawals            │
├─────────────────────────────────┤       ├─────────────────────────────────┤
│ id          INTEGER (PK)        │───┐   │ id              INTEGER (PK)    │
│ plan_type   VARCHAR(50) NOT NULL│   │   │ time_deposit_id INTEGER (FK) NN │
│ balance     DECIMAL(19,2) NN    │   └──▶│ amount          DECIMAL(19,2)NN │
│ days        INTEGER NOT NULL    │       │ withdrawal_date DATE NOT NULL   │
│ created_at  TIMESTAMP           │       │ created_at      TIMESTAMP       │
│ updated_at  TIMESTAMP           │       └─────────────────────────────────┘
└─────────────────────────────────┘
```

### 2.2 SQL Schema (Flyway Migration)

```sql
-- V1__create_time_deposits_table.sql
CREATE TABLE time_deposits (
    id SERIAL PRIMARY KEY,
    plan_type VARCHAR(50) NOT NULL CHECK (plan_type IN ('basic', 'student', 'premium')),
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    days INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_time_deposits_plan_type ON time_deposits(plan_type);

-- V2__create_withdrawals_table.sql
CREATE TABLE withdrawals (
    id SERIAL PRIMARY KEY,
    time_deposit_id INTEGER NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    withdrawal_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_time_deposit
        FOREIGN KEY (time_deposit_id)
        REFERENCES time_deposits(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_withdrawals_time_deposit_id ON withdrawals(time_deposit_id);
```

---

## 3. API Endpoint Specifications

### 3.1 OpenAPI Contract Summary

| Endpoint | Method | Description | Request Body | Response |
|----------|--------|-------------|--------------|----------|
| `/api/v1/time-deposits` | GET | Retrieve all time deposits with withdrawals | None | `200`: List of TimeDepositDTO |
| `/api/v1/time-deposits/update-balances` | POST | Update balances for all deposits | None | `200`: Success message |

### 3.2 Detailed API Specifications

#### GET /api/v1/time-deposits

**Response Schema (200 OK):**
```json
[
  {
    "id": 1,
    "planType": "basic",
    "balance": 1234567.00,
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

#### POST /api/v1/time-deposits/update-balances

**Response Schema (200 OK):**
```json
{
  "message": "Balances updated successfully",
  "updatedCount": 5,
  "timestamp": "2024-01-08T10:30:00Z"
}
```

---

## 4. Security Measures and Tools

### 4.1 Security Tools Integration

| Tool | Purpose | Integration Point |
|------|---------|-------------------|
| **OWASP Dependency-Check** | Scan dependencies for known vulnerabilities (CVEs) | Maven build, CI/CD pipeline |
| **SpotBugs + FindSecBugs** | Static analysis for security bugs | Maven build phase |
| **Trivy** | Container image vulnerability scanning | Docker build, CI/CD |
| **SonarQube** | Code quality and security hotspots | CI/CD pipeline |

### 4.2 Security Best Practices Implementation

```yaml
Security Checklist:
  - [ ] Input validation on all endpoints (Bean Validation)
  - [ ] SQL injection prevention (JPA parameterized queries)
  - [ ] HTTPS enforcement in production
  - [ ] Rate limiting on API endpoints
  - [ ] Security headers (CORS, CSP, X-Frame-Options)
  - [ ] Actuator endpoints secured
  - [ ] Sensitive data masking in logs
  - [ ] Dependency vulnerability scanning in CI/CD
```

### 4.3 Maven Security Plugin Configuration

```xml
<!-- OWASP Dependency-Check -->
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>9.0.9</version>
    <configuration>
        <failBuildOnCVSS>7</failBuildOnCVSS>
        <suppressionFiles>
            <suppressionFile>owasp-suppressions.xml</suppressionFile>
        </suppressionFiles>
    </configuration>
</plugin>

<!-- SpotBugs with FindSecBugs -->
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.8.3.0</version>
    <configuration>
        <plugins>
            <plugin>
                <groupId>com.h3xstream.findsecbugs</groupId>
                <artifactId>findsecbugs-plugin</artifactId>
                <version>1.12.0</version>
            </plugin>
        </plugins>
    </configuration>
</plugin>
```

---

## 5. Testing Strategy

### 5.1 Test Pyramid

```
                    ┌───────────────┐
                    │   E2E Tests   │  (Manual/Swagger UI)
                    │     ~5%       │
                    └───────┬───────┘
                    ┌───────▼───────────────┐
                    │  Integration Tests    │
                    │  (Testcontainers)     │
                    │        ~25%           │
                    └───────┬───────────────┘
        ┌───────────────────▼───────────────────────────┐
        │              Unit Tests                        │
        │   (Domain logic, Services, Mappers)           │
        │                  ~70%                          │
        └────────────────────────────────────────────────┘
```

### 5.2 Test Categories and Coverage Goals

| Category | Coverage Goal | Tools | Focus Areas |
|----------|---------------|-------|-------------|
| **Unit Tests** | ≥80% | JUnit 5, Mockito, AssertJ | Business logic, interest calculations, mappers |
| **Integration Tests** | ≥70% | Testcontainers, Spring Boot Test | Repository, API endpoints, DB transactions |
| **Contract Tests** | 100% endpoints | Spring REST Docs / OpenAPI | API contract validation |

### 5.3 Testcontainers Configuration

```java
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimeDepositIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("timedeposit_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

### 5.4 Test Cases Matrix

#### Unit Tests for TimeDepositCalculator (Existing Logic - Regression)

| Test Case | Plan Type | Days | Initial Balance | Expected Interest | Notes |
|-----------|-----------|------|-----------------|-------------------|-------|
| No interest first 30 days | basic | 30 | 10000.00 | 0.00 | Grace period |
| Basic interest after 30 days | basic | 31 | 12000.00 | 10.00 | 1%/12 months |
| Student interest under 1 year | student | 100 | 12000.00 | 30.00 | 3%/12 months |
| Student no interest after 1 year | student | 366 | 12000.00 | 0.00 | Cap at 365 days |
| Premium no interest before 45 days | premium | 44 | 12000.00 | 0.00 | 45-day wait |
| Premium interest after 45 days | premium | 46 | 12000.00 | 50.00 | 5%/12 months |

#### Integration Tests

| Test Case | Endpoint | Scenario | Expected Result |
|-----------|----------|----------|-----------------|
| Get all deposits | GET /api/v1/time-deposits | Multiple deposits with withdrawals | 200, correct JSON structure |
| Get empty list | GET /api/v1/time-deposits | No deposits in DB | 200, empty array |
| Update balances | POST /api/v1/time-deposits/update-balances | Multiple deposits | 200, all balances updated |

---

## 6. CI/CD Pipeline for Continuous Compliance

### 6.1 GitHub Actions Workflow

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Build and Test
        run: mvn clean verify -Ptest

      - name: Upload Test Results
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: target/surefire-reports/

  security-scan:
    runs-on: ubuntu-latest
    needs: build-and-test
    steps:
      - uses: actions/checkout@v4

      - name: OWASP Dependency Check
        run: mvn org.owasp:dependency-check-maven:check

      - name: Upload OWASP Report
        uses: actions/upload-artifact@v4
        with:
          name: owasp-report
          path: target/dependency-check-report.html

  code-quality:
    runs-on: ubuntu-latest
    needs: build-and-test
    steps:
      - uses: actions/checkout@v4

      - name: SpotBugs Security Analysis
        run: mvn spotbugs:check

      - name: SonarQube Scan
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: mvn sonar:sonar

  docker-build:
    runs-on: ubuntu-latest
    needs: [security-scan, code-quality]
    steps:
      - uses: actions/checkout@v4

      - name: Build Docker Image
        run: docker build -t time-deposit-api:${{ github.sha }} .

      - name: Trivy Container Scan
        uses: aquasecurity/trivy-action@master
        with:
          image-ref: 'time-deposit-api:${{ github.sha }}'
          format: 'table'
          exit-code: '1'
          severity: 'CRITICAL,HIGH'
```

### 6.2 Pipeline Stages

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Build &   │───▶│  Security   │───▶│    Code     │───▶│   Docker    │
│    Test     │    │    Scan     │    │   Quality   │    │    Build    │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
      │                  │                  │                  │
      ▼                  ▼                  ▼                  ▼
 Unit Tests        OWASP Check        SpotBugs          Trivy Scan
 Integration       CVE Analysis       SonarQube        Image Security
```

---

## 7. Development Phases and Milestones

### Phase 1: Foundation Setup (Day 1)

| Task | Description | Deliverable |
|------|-------------|-------------|
| 1.1 | Convert to Spring Boot project | Updated pom.xml with Spring Boot parent |
| 1.2 | Add required dependencies | Spring Web, JPA, PostgreSQL, Flyway, Swagger |
| 1.3 | Create Hexagonal package structure | Empty package directories |
| 1.4 | Setup Docker Compose for PostgreSQL | docker-compose.yml |
| 1.5 | Configure application properties | application.yml, profiles |

**Milestone 1**: Project builds and connects to PostgreSQL container

### Phase 2: Database Layer (Day 1-2)

| Task | Description | Deliverable |
|------|-------------|-------------|
| 2.1 | Create JPA entities | TimeDepositEntity, WithdrawalEntity |
| 2.2 | Create Flyway migrations | V1, V2 SQL files |
| 2.3 | Implement repository interfaces | JpaTimeDepositRepository, JpaWithdrawalRepository |
| 2.4 | Add sample data migration | V3 seed data SQL |

**Milestone 2**: Database schema created, entities persisted correctly

### Phase 3: Domain Service Layer (Day 2)

| Task | Description | Deliverable |
|------|-------------|-------------|
| 3.1 | Create DTOs | TimeDepositResponseDTO, WithdrawalDTO |
| 3.2 | Create mappers | TimeDepositMapper (Entity ↔ DTO ↔ Domain) |
| 3.3 | Implement TimeDepositService | Business logic coordinating existing calculator |
| 3.4 | Unit tests for service layer | ServiceTest classes |

**Milestone 3**: Service layer complete with unit tests passing

### Phase 4: REST API Layer (Day 2-3)

| Task | Description | Deliverable |
|------|-------------|-------------|
| 4.1 | Create REST controller | TimeDepositController |
| 4.2 | Configure OpenAPI/Swagger | OpenApiConfig, annotations |
| 4.3 | Integration tests with Testcontainers | Controller integration tests |
| 4.4 | API documentation | Swagger UI accessible |

**Milestone 4**: Both API endpoints functional, Swagger UI accessible

### Phase 5: Security & Compliance (Day 3)

| Task | Description | Deliverable |
|------|-------------|-------------|
| 5.1 | Add security scanning plugins | pom.xml updates |
| 5.2 | Configure OWASP Dependency-Check | Plugin configuration, suppressions |
| 5.3 | Add SpotBugs/FindSecBugs | Static analysis configuration |
| 5.4 | Security scan execution | Clean security reports |

**Milestone 5**: All security scans pass, no critical vulnerabilities

### Phase 6: Containerization & Documentation (Day 3)

| Task | Description | Deliverable |
|------|-------------|-------------|
| 6.1 | Create Dockerfile | Multi-stage Dockerfile |
| 6.2 | Update docker-compose | Full stack compose file |
| 6.3 | Create CI/CD pipeline | GitHub Actions workflow |
| 6.4 | Final documentation | Updated README with instructions |

**Milestone 6**: Full containerized deployment, CI/CD pipeline green

---

## 8. Dependencies and Prerequisites

### 8.1 Development Environment

| Requirement | Version | Purpose |
|-------------|---------|---------|
| Java JDK | 17+ | Runtime and compilation |
| Maven | 3.8+ | Build tool |
| Docker | 24+ | Containerization |
| Docker Compose | 2.x | Multi-container orchestration |
| Git | 2.x | Version control |
| IDE | IntelliJ/VSCode | Development |

### 8.2 Maven Dependencies to Add

```xml
<!-- Spring Boot Starter Parent -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.1</version>
</parent>

<!-- Core Dependencies -->
<dependencies>
    <!-- Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Flyway -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>

    <!-- OpenAPI/Swagger -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.3.0</version>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Actuator -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Testcontainers -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <version>1.19.3</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>1.19.3</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 8.3 Docker Configuration Files

#### Dockerfile

```dockerfile
# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### docker-compose.yml

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: timedeposit-db
    environment:
      POSTGRES_DB: timedeposit
      POSTGRES_USER: timedeposit
      POSTGRES_PASSWORD: timedeposit123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U timedeposit"]
      interval: 5s
      timeout: 5s
      retries: 5

  app:
    build: .
    container_name: timedeposit-api
    depends_on:
      postgres:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/timedeposit
      SPRING_DATASOURCE_USERNAME: timedeposit
      SPRING_DATASOURCE_PASSWORD: timedeposit123
    ports:
      - "8080:8080"

volumes:
  postgres_data:
```

---

## 9. Error Handling and Logging

### 9.1 Logging Configuration

```yaml
# application.yml
logging:
  level:
    root: INFO
    org.ikigaidigital: DEBUG
    org.springframework.web: INFO
    org.hibernate.SQL: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/timedeposit.log
```

### 9.2 Global Exception Handler (Optional Enhancement)

Note: Per requirements, exception handling is not required, but this provides a template for future enhancement.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(500)
            .body(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
```

---

## 10. Key Design Decisions and Assumptions

### 10.1 Assumptions Made

| # | Assumption | Rationale |
|---|------------|-----------|
| 1 | Interest is calculated monthly | "monthly interest" in requirements |
| 2 | `updateBalance` endpoint updates all deposits atomically | No partial update semantics specified |
| 3 | Days are cumulative, not reset | Existing logic treats days as total elapsed |
| 4 | Withdrawals are read-only in this scope | No CRUD for withdrawals required |
| 5 | Plan types are case-sensitive lowercase | Existing code uses lowercase strings |

### 10.2 Key Design Decisions

| Decision | Alternative Considered | Reason for Choice |
|----------|----------------------|-------------------|
| Use Flyway over Liquibase | Liquibase | Simpler for this project size, SQL-native |
| springdoc-openapi over SpringFox | SpringFox | Better Spring Boot 3.x support |
| Strategy pattern for interest | Switch-case refactor | Extensibility requirement from README |
| MapStruct for mapping | Manual mapping | Compile-time safety, reduced boilerplate |

---

## 11. API Triggering Instructions (Swagger)

### 11.1 Starting the Application

```bash
# Option 1: Docker Compose (Recommended)
docker-compose up -d

# Option 2: Local with external PostgreSQL
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### 11.2 Accessing Swagger UI

1. Navigate to: `http://localhost:8080/swagger-ui.html`
2. Expand the `time-deposit-controller` section
3. Available endpoints:
   - **GET** `/api/v1/time-deposits` - Click "Try it out" → "Execute"
   - **POST** `/api/v1/time-deposits/update-balances` - Click "Try it out" → "Execute"

### 11.3 Alternative: cURL Commands

```bash
# Get all time deposits
curl -X GET http://localhost:8080/api/v1/time-deposits \
  -H "Accept: application/json"

# Update all balances
curl -X POST http://localhost:8080/api/v1/time-deposits/update-balances \
  -H "Accept: application/json"
```

---

## 12. Summary and Next Steps

This implementation plan provides a comprehensive roadmap for completing the Time Deposit system while:

✅ **Preserving existing functionality** - `TimeDeposit` class and `updateBalance` method unchanged
✅ **Following Hexagonal Architecture** - Clear separation of concerns
✅ **Implementing security scanning** - OWASP, SpotBugs, Trivy integration
✅ **Comprehensive testing** - Unit tests, integration tests with Testcontainers
✅ **Containerization** - Docker and Docker Compose for easy deployment
✅ **API Documentation** - OpenAPI/Swagger contract
✅ **CI/CD Pipeline** - GitHub Actions for continuous compliance

### Immediate Next Steps

1. Review and approve this implementation plan
2. Begin Phase 1: Foundation Setup
3. Execute phases sequentially with atomic commits
4. Validate each milestone before proceeding

