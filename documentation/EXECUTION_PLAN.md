# Time Deposit System - Detailed Execution Plan

## Overview

This document provides a granular, step-by-step execution plan with atomic commits for implementing the Time Deposit system. Each commit represents a single logical change that leaves the codebase in a buildable, testable state.

**Total Estimated Commits:** 32
**Estimated Duration:** 3 days

---

## Commit Naming Convention

All commits follow the **Conventional Commits** format:

```
type(scope): description

Types:
- feat:     New feature
- fix:      Bug fix
- refactor: Code refactoring
- test:     Adding/updating tests
- docs:     Documentation
- chore:    Build, config, dependencies
- ci:       CI/CD pipeline changes
```

---

## Pre-Implementation Checklist

Before starting, ensure:
- [ ] Java 17+ installed (`java -version`)
- [ ] Maven 3.8+ installed (`mvn -version`)
- [ ] Docker installed and running (`docker --version`)
- [ ] Git configured (`git config --list`)
- [ ] Repository forked and cloned

---

## Phase 1: Foundation Setup

### Commit 1: `chore(build): convert project to Spring Boot with parent POM`

**Files Modified:**
- `pom.xml`

**Changes:**
- Add Spring Boot parent POM (version 3.2.1)
- Update Maven compiler plugin configuration
- Keep existing JUnit and AssertJ dependencies

**Verification:**
```bash
mvn clean compile
# Expected: BUILD SUCCESS
```

---

### Commit 2: `chore(deps): add Spring Boot web and core dependencies`

**Files Modified:**
- `pom.xml`

**Changes:**
- Add `spring-boot-starter-web`
- Add `spring-boot-starter-validation`
- Add `spring-boot-starter-actuator`

**Verification:**
```bash
mvn clean compile
# Expected: BUILD SUCCESS, Spring dependencies downloaded
```

---

### Commit 3: `chore(deps): add database dependencies (JPA, PostgreSQL, Flyway)`

**Files Modified:**
- `pom.xml`

**Changes:**
- Add `spring-boot-starter-data-jpa`
- Add `postgresql` driver (runtime scope)
- Add `flyway-core` and `flyway-database-postgresql`

**Verification:**
```bash
mvn dependency:tree | grep -E "(jpa|postgresql|flyway)"
# Expected: All three dependencies listed
```

---

### Commit 4: `chore(deps): add OpenAPI/Swagger documentation dependency`

**Files Modified:**
- `pom.xml`

**Changes:**
- Add `springdoc-openapi-starter-webmvc-ui` (version 2.3.0)

**Verification:**
```bash
mvn dependency:resolve
# Expected: BUILD SUCCESS
```

---

### Commit 5: `chore(deps): add test dependencies (Spring Boot Test, Testcontainers)`

**Files Modified:**
- `pom.xml`

**Changes:**
- Add `spring-boot-starter-test`
- Add `testcontainers` BOM for version management
- Add `testcontainers` postgresql module
- Add `testcontainers` junit-jupiter module

**Verification:**
```bash
mvn dependency:tree | grep testcontainers
# Expected: Testcontainers dependencies listed
```

---

### Commit 6: `feat(app): create Spring Boot application entry point`

**Files Created:**
- `src/main/java/org/ikigaidigital/TimeDepositApplication.java`

**Changes:**
- Create main application class with `@SpringBootApplication`
- Add main method with `SpringApplication.run()`

**Verification:**
```bash
mvn clean compile
# Expected: BUILD SUCCESS (won't run yet - no DB config)
```

---

### Commit 7: `chore(config): add application configuration files`

**Files Created:**
- `src/main/resources/application.yml`
- `src/main/resources/application-local.yml`
- `src/main/resources/application-docker.yml`

**Changes:**
- Configure default profile settings
- Configure PostgreSQL datasource for each profile
- Configure Flyway settings
- Configure logging levels
- Configure OpenAPI/Swagger settings

**Verification:**
```bash
cat src/main/resources/application.yml
# Expected: Configuration file with proper YAML structure
```

---

### Commit 8: `chore(docker): add Docker Compose for local PostgreSQL`

**Files Created:**
- `docker-compose.yml`

**Changes:**
- Define PostgreSQL 15 service
- Configure database name, user, password
- Add health check configuration
- Define persistent volume

**Verification:**
```bash
docker-compose up -d postgres
docker-compose ps
# Expected: postgres service running, healthy
docker-compose down
```

---

### Commit 9: `chore(structure): create hexagonal architecture package structure`

**Files Created:**
- `src/main/java/org/ikigaidigital/domain/model/.gitkeep`
- `src/main/java/org/ikigaidigital/domain/port/input/.gitkeep`
- `src/main/java/org/ikigaidigital/domain/port/output/.gitkeep`
- `src/main/java/org/ikigaidigital/domain/service/.gitkeep`
- `src/main/java/org/ikigaidigital/infrastructure/adapter/input/rest/.gitkeep`
- `src/main/java/org/ikigaidigital/infrastructure/adapter/input/rest/dto/.gitkeep`
- `src/main/java/org/ikigaidigital/infrastructure/adapter/output/persistence/.gitkeep`
- `src/main/java/org/ikigaidigital/infrastructure/config/.gitkeep`
- `src/main/java/org/ikigaidigital/shared/mapper/.gitkeep`

**Changes:**
- Create empty directories with .gitkeep files to establish package structure

**Verification:**
```bash
find src/main/java/org/ikigaidigital -type d | sort
# Expected: All hexagonal architecture directories listed
```

**Milestone 1 Complete:** Project structure established, dependencies configured

---

## Phase 2: Database Layer

### Commit 10: `feat(database): add Flyway migration for time_deposits table`

**Files Created:**
- `src/main/resources/db/migration/V1__create_time_deposits_table.sql`

**Changes:**
- Create `time_deposits` table with columns: id, plan_type, balance, days, created_at, updated_at
- Add CHECK constraint for valid plan types (basic, student, premium)
- Add index on plan_type column

**Verification:**
```bash
docker-compose up -d postgres
mvn spring-boot:run -Dspring-boot.run.profiles=local &
# Wait for startup, then check logs for "Successfully applied 1 migration"
# Ctrl+C to stop
```

---

### Commit 11: `feat(database): add Flyway migration for withdrawals table`

**Files Created:**
- `src/main/resources/db/migration/V2__create_withdrawals_table.sql`

**Changes:**
- Create `withdrawals` table with columns: id, time_deposit_id, amount, withdrawal_date, created_at
- Add foreign key constraint to time_deposits table
- Add index on time_deposit_id column

**Verification:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local &
# Check logs for "Successfully applied 2 migrations"
```

---

### Commit 12: `feat(database): add seed data migration for testing`

**Files Created:**
- `src/main/resources/db/migration/V3__seed_sample_data.sql`

**Changes:**
- Insert sample time deposits (one of each plan type)
- Insert sample withdrawals linked to deposits

**Verification:**
```bash
# Connect to database and verify data
docker-compose exec postgres psql -U timedeposit -d timedeposit -c "SELECT * FROM time_deposits;"
# Expected: 3 rows of sample data
```

---

### Commit 13: `feat(entity): create TimeDepositEntity JPA entity`

**Files Created:**
- `src/main/java/org/ikigaidigital/domain/model/TimeDepositEntity.java`

**Changes:**
- Create JPA entity mapping to `time_deposits` table
- Add @Entity, @Table, @Id, @GeneratedValue annotations
- Add @Column mappings for all fields
- Add @OneToMany relationship to WithdrawalEntity (to be created)
- Include getters, setters, constructors

**Verification:**
```bash
mvn clean compile
# Expected: BUILD SUCCESS
```

---

### Commit 14: `feat(entity): create WithdrawalEntity JPA entity`

**Files Created:**
- `src/main/java/org/ikigaidigital/domain/model/WithdrawalEntity.java`

**Changes:**
- Create JPA entity mapping to `withdrawals` table
- Add @Entity, @Table, @Id, @GeneratedValue annotations
- Add @ManyToOne relationship to TimeDepositEntity
- Add @Column mappings for amount, withdrawal_date
- Include getters, setters, constructors

**Verification:**
```bash
mvn clean compile
# Expected: BUILD SUCCESS
```

---

### Commit 15: `feat(repository): create TimeDepositRepository interface`

**Files Created:**
- `src/main/java/org/ikigaidigital/domain/port/output/TimeDepositRepositoryPort.java`
- `src/main/java/org/ikigaidigital/infrastructure/adapter/output/persistence/JpaTimeDepositRepository.java`

**Changes:**
- Create output port interface (domain layer)
- Create Spring Data JPA repository extending JpaRepository
- Repository should fetch deposits with eagerly loaded withdrawals

**Verification:**
```bash
mvn clean compile
# Expected: BUILD SUCCESS
```

---

### Commit 16: `feat(repository): create WithdrawalRepository interface`

**Files Created:**
- `src/main/java/org/ikigaidigital/domain/port/output/WithdrawalRepositoryPort.java`
- `src/main/java/org/ikigaidigital/infrastructure/adapter/output/persistence/JpaWithdrawalRepository.java`

**Changes:**
- Create output port interface (domain layer)
- Create Spring Data JPA repository extending JpaRepository
- Add method to find withdrawals by time deposit ID

**Verification:**
```bash
mvn clean compile
# Expected: BUILD SUCCESS
```

---

### Commit 17: `test(repository): add integration tests for repositories`

**Files Created:**
- `src/test/java/org/ikigaidigital/infrastructure/adapter/output/persistence/TimeDepositRepositoryIntegrationTest.java`

**Changes:**
- Create base test configuration with Testcontainers PostgreSQL
- Test save and find operations for TimeDepositEntity
- Test cascading to WithdrawalEntity
- Use @DataJpaTest with Testcontainers

**Verification:**
```bash
mvn test -Dtest=TimeDepositRepositoryIntegrationTest
# Expected: All tests pass
```

**Milestone 2 Complete:** Database layer with migrations and repositories working

---

## Phase 3: Domain Service Layer

### Commit 18: `feat(dto): create WithdrawalDTO for API responses`

**Files Created:**
- `src/main/java/org/ikigaidigital/infrastructure/adapter/input/rest/dto/WithdrawalDTO.java`

**Changes:**
- Create DTO record/class with fields: id, amount, date
- Add OpenAPI annotations for documentation
- Use LocalDate for date field

**Verification:**
```bash
mvn clean compile
# Expected: BUILD SUCCESS
```

---

### Commit 19: `feat(dto): create TimeDepositResponseDTO for API responses`

**Files Created:**
- `src/main/java/org/ikigaidigital/infrastructure/adapter/input/rest/dto/TimeDepositResponseDTO.java`

**Changes:**
- Create DTO record/class with fields: id, planType, balance, days, withdrawals
- Withdrawals field is List<WithdrawalDTO>
- Add OpenAPI annotations (@Schema) for documentation

**Verification:**
```bash
mvn clean compile
# Expected: BUILD SUCCESS
```

---

### Commit 20: `feat(dto): create UpdateBalancesResponseDTO for API responses`

**Files Created:**
- `src/main/java/org/ikigaidigital/infrastructure/adapter/input/rest/dto/UpdateBalancesResponseDTO.java`

**Changes:**
- Create DTO with fields: message, updatedCount, timestamp
- Add OpenAPI annotations for documentation

**Verification:**
```bash
mvn clean compile
# Expected: BUILD SUCCESS
```

---

### Commit 21: `feat(mapper): create TimeDepositMapper for entity-DTO conversion`

**Files Created:**
- `src/main/java/org/ikigaidigital/shared/mapper/TimeDepositMapper.java`

**Changes:**
- Create mapper class with @Component annotation
- Method: `toDTO(TimeDepositEntity entity)` → `TimeDepositResponseDTO`
- Method: `toDTOList(List<TimeDepositEntity> entities)` → `List<TimeDepositResponseDTO>`
- Method: `toDomain(TimeDepositEntity entity)` → `TimeDeposit` (existing class)
- Method: `toDomainList(List<TimeDepositEntity> entities)` → `List<TimeDeposit>`
- Method: `updateEntityFromDomain(TimeDepositEntity entity, TimeDeposit domain)`

**Verification:**
```bash
mvn clean compile
# Expected: BUILD SUCCESS
```

---

### Commit 22: `test(mapper): add unit tests for TimeDepositMapper`

**Files Created:**
- `src/test/java/org/ikigaidigital/shared/mapper/TimeDepositMapperTest.java`

**Changes:**
- Test toDTO conversion with withdrawals
- Test toDTOList for multiple entities
- Test toDomain conversion
- Test updateEntityFromDomain updates balance correctly

**Verification:**
```bash
mvn test -Dtest=TimeDepositMapperTest
# Expected: All tests pass
```

---

### Commit 23: `feat(port): create TimeDepositService input port interface`

**Files Created:**
- `src/main/java/org/ikigaidigital/domain/port/input/TimeDepositServicePort.java`

**Changes:**
- Create interface defining use cases:
  - `List<TimeDepositResponseDTO> getAllTimeDeposits()`
  - `UpdateBalancesResponseDTO updateAllBalances()`

**Verification:**
```bash
mvn clean compile
# Expected: BUILD SUCCESS
```

---

### Commit 24: `feat(service): implement TimeDepositServiceImpl`

**Files Created:**
- `src/main/java/org/ikigaidigital/domain/service/TimeDepositServiceImpl.java`

**Changes:**
- Implement TimeDepositServicePort interface
- Inject TimeDepositRepositoryPort (or JPA repository)
- Inject TimeDepositMapper
- Inject existing TimeDepositCalculator (via constructor or @Autowired)
- `getAllTimeDeposits()`: fetch all entities, map to DTOs, return
- `updateAllBalances()`:
  1. Fetch all entities
  2. Map to domain objects (List<TimeDeposit>)
  3. Call existing `TimeDepositCalculator.updateBalance()`
  4. Update entities with new balances
  5. Save all entities
  6. Return response DTO with count

**Verification:**
```bash
mvn clean compile
# Expected: BUILD SUCCESS
```

---

### Commit 25: `refactor(calculator): make TimeDepositCalculator a Spring bean`

**Files Modified:**
- `src/main/java/org/ikigaidigital/TimeDepositCalculator.java`

**Changes:**
- Add @Component annotation to class
- **DO NOT** change the `updateBalance` method signature

**Verification:**
```bash
mvn clean compile
mvn test -Dtest=TimeDepositCalculatorTest
# Expected: Existing test still passes
```

---

### Commit 26: `test(service): add unit tests for TimeDepositServiceImpl`

**Files Created:**
- `src/test/java/org/ikigaidigital/domain/service/TimeDepositServiceImplTest.java`

**Changes:**
- Use Mockito to mock repository and mapper
- Test `getAllTimeDeposits()` returns correctly mapped DTOs
- Test `updateAllBalances()` calls calculator and saves entities
- Verify no changes to calculator behavior

**Verification:**
```bash
mvn test -Dtest=TimeDepositServiceImplTest
# Expected: All tests pass
```

**Milestone 3 Complete:** Service layer with business logic working

---

## Phase 4: REST API Layer

### Commit 27: `feat(config): add OpenAPI/Swagger configuration`

**Files Created:**
- `src/main/java/org/ikigaidigital/infrastructure/config/OpenApiConfig.java`

**Changes:**
- Create @Configuration class with OpenAPI bean
- Configure API info: title, description, version
- Configure contact and license info
- Add server URLs for different environments

**Verification:**
```bash
mvn clean compile
# Expected: BUILD SUCCESS
```

---

### Commit 28: `feat(api): implement GET /api/v1/time-deposits endpoint`

**Files Created:**
- `src/main/java/org/ikigaidigital/infrastructure/adapter/input/rest/TimeDepositController.java`

**Changes:**
- Create @RestController with @RequestMapping("/api/v1/time-deposits")
- Inject TimeDepositServicePort
- Implement GET endpoint returning List<TimeDepositResponseDTO>
- Add OpenAPI annotations: @Operation, @ApiResponses, @Tag
- Response includes: id, planType, balance, days, withdrawals

**Verification:**
```bash
docker-compose up -d postgres
mvn spring-boot:run -Dspring-boot.run.profiles=local &
curl http://localhost:8080/api/v1/time-deposits
# Expected: JSON array of time deposits
```

---

### Commit 29: `feat(api): implement POST /api/v1/time-deposits/update-balances endpoint`

**Files Modified:**
- `src/main/java/org/ikigaidigital/infrastructure/adapter/input/rest/TimeDepositController.java`

**Changes:**
- Add POST endpoint at `/update-balances`
- Call service.updateAllBalances()
- Return UpdateBalancesResponseDTO
- Add OpenAPI annotations for documentation

**Verification:**
```bash
curl -X POST http://localhost:8080/api/v1/time-deposits/update-balances
# Expected: JSON with message, updatedCount, timestamp
curl http://localhost:8080/api/v1/time-deposits
# Expected: Balances updated (if > 30 days)
```

---

### Commit 30: `test(api): add integration tests for TimeDepositController`

**Files Created:**
- `src/test/java/org/ikigaidigital/infrastructure/adapter/input/rest/TimeDepositControllerIntegrationTest.java`

**Changes:**
- Use @SpringBootTest with Testcontainers
- Use TestRestTemplate or MockMvc
- Test GET /api/v1/time-deposits returns correct data
- Test POST /api/v1/time-deposits/update-balances updates balances
- Verify response schema matches requirements

**Verification:**
```bash
mvn test -Dtest=TimeDepositControllerIntegrationTest
# Expected: All tests pass
```

---

### Commit 31: `test(calculator): enhance existing calculator tests for regression`

**Files Modified:**
- `src/test/java/org/ikigaidigital/TimeDepositCalculatorTest.java`

**Changes:**
- Add comprehensive test cases per test matrix:
  - No interest first 30 days (all plan types)
  - Basic plan interest after 30 days
  - Student plan interest (under 366 days)
  - Student plan no interest (366+ days)
  - Premium plan no interest (before 46 days)
  - Premium plan interest (after 45 days)
- Verify exact balance calculations

**Verification:**
```bash
mvn test -Dtest=TimeDepositCalculatorTest
# Expected: All tests pass, verifying existing logic preserved
```

**Milestone 4 Complete:** Both API endpoints functional, Swagger UI accessible

**Swagger UI Verification:**
```bash
# Navigate to: http://localhost:8080/swagger-ui.html
# Expected: See both endpoints documented
# Try "Execute" for each endpoint
```

---

## Phase 5: Security & Compliance

### Commit 32: `chore(security): add OWASP Dependency-Check plugin`

**Files Modified:**
- `pom.xml`

**Files Created:**
- `owasp-suppressions.xml`

**Changes:**
- Add OWASP dependency-check-maven plugin
- Configure to fail build on CVSS score >= 7
- Create suppressions file for known false positives

**Verification:**
```bash
mvn org.owasp:dependency-check-maven:check
# Expected: Report generated, no critical vulnerabilities (or documented suppressions)
# Report at: target/dependency-check-report.html
```

---

### Commit 33: `chore(security): add SpotBugs with FindSecBugs plugin`

**Files Modified:**
- `pom.xml`

**Files Created:**
- `spotbugs-exclude.xml`

**Changes:**
- Add SpotBugs Maven plugin
- Add FindSecBugs plugin for security analysis
- Create exclusion file for known non-issues
- Configure to run during verify phase

**Verification:**
```bash
mvn spotbugs:check
# Expected: No bugs found or documented exclusions
```

---

### Commit 34: `chore(security): add JaCoCo for test coverage reporting`

**Files Modified:**
- `pom.xml`

**Changes:**
- Add JaCoCo Maven plugin
- Configure for 80% line coverage minimum
- Generate reports in target/site/jacoco

**Verification:**
```bash
mvn clean verify
# Expected: Coverage report generated
# Report at: target/site/jacoco/index.html
```

**Milestone 5 Complete:** All security scans pass, coverage reports generated

---

## Phase 6: Containerization & CI/CD

### Commit 35: `chore(docker): create multi-stage Dockerfile`

**Files Created:**
- `Dockerfile`

**Changes:**
- Create multi-stage Dockerfile:
  - Build stage: Maven build with JDK 17
  - Runtime stage: JRE 17 Alpine
- Add non-root user for security
- Configure proper ENTRYPOINT

**Verification:**
```bash
docker build -t time-deposit-api:test .
# Expected: Image builds successfully
docker images | grep time-deposit-api
# Expected: Image listed
```

---

### Commit 36: `chore(docker): update docker-compose for full stack deployment`

**Files Modified:**
- `docker-compose.yml`

**Changes:**
- Add application service with build context
- Configure depends_on with health check
- Add environment variables for database connection
- Add proper networking between services

**Verification:**
```bash
docker-compose up --build -d
docker-compose ps
# Expected: Both postgres and app services running
curl http://localhost:8080/api/v1/time-deposits
# Expected: JSON response
docker-compose down
```

---

### Commit 37: `ci: add GitHub Actions CI/CD workflow`

**Files Created:**
- `.github/workflows/ci.yml`

**Changes:**
- Create workflow triggered on push/PR to main
- Add jobs: build-and-test, security-scan, code-quality, docker-build
- Configure Java 17 with Maven caching
- Add artifact uploads for test results and reports
- Add Trivy scan for Docker image

**Verification:**
```bash
# Push to GitHub and verify Actions tab
# Expected: Workflow runs successfully
```

---

### Commit 38: `docs: update README with setup and usage instructions`

**Files Modified:**
- `README.md`

**Changes:**
- Add "Getting Started" section
- Add Docker Compose quick start instructions
- Add API documentation (Swagger UI link)
- Add development setup instructions
- Add testing instructions
- Keep original requirements section

**Verification:**
```bash
# Read README.md
# Follow instructions to verify they work
```

---

### Commit 39: `chore: add .gitignore for Java/Maven/IDE files`

**Files Created/Modified:**
- `.gitignore`

**Changes:**
- Add Maven target directory
- Add IDE files (IntelliJ, Eclipse, VSCode)
- Add OS-specific files
- Add log files

**Verification:**
```bash
git status
# Expected: No unwanted files tracked
```

**Milestone 6 Complete:** Full containerized deployment, CI/CD pipeline configured

---

## Post-Implementation Verification

### Full System Test

```bash
# 1. Start full stack
docker-compose up --build -d

# 2. Wait for services to be healthy
docker-compose ps

# 3. Verify Swagger UI
open http://localhost:8080/swagger-ui.html

# 4. Test GET endpoint
curl -s http://localhost:8080/api/v1/time-deposits | jq .

# 5. Test POST endpoint
curl -s -X POST http://localhost:8080/api/v1/time-deposits/update-balances | jq .

# 6. Verify balances updated
curl -s http://localhost:8080/api/v1/time-deposits | jq .

# 7. Run all tests
docker-compose down
mvn clean verify

# 8. Check coverage
open target/site/jacoco/index.html
```

### Final Checklist

- [ ] All 39 commits completed
- [ ] Existing TimeDeposit.java unchanged
- [ ] Existing TimeDepositCalculator.updateBalance() signature unchanged
- [ ] Exactly 2 API endpoints implemented
- [ ] All tests pass
- [ ] Coverage >= 80%
- [ ] Security scans pass
- [ ] Docker compose works
- [ ] Swagger UI accessible
- [ ] README updated

---

## Commit Summary Table

| # | Commit Message | Phase | Files Changed |
|---|----------------|-------|---------------|
| 1 | `chore(build): convert project to Spring Boot with parent POM` | 1 | pom.xml |
| 2 | `chore(deps): add Spring Boot web and core dependencies` | 1 | pom.xml |
| 3 | `chore(deps): add database dependencies (JPA, PostgreSQL, Flyway)` | 1 | pom.xml |
| 4 | `chore(deps): add OpenAPI/Swagger documentation dependency` | 1 | pom.xml |
| 5 | `chore(deps): add test dependencies (Spring Boot Test, Testcontainers)` | 1 | pom.xml |
| 6 | `feat(app): create Spring Boot application entry point` | 1 | TimeDepositApplication.java |
| 7 | `chore(config): add application configuration files` | 1 | application*.yml |
| 8 | `chore(docker): add Docker Compose for local PostgreSQL` | 1 | docker-compose.yml |
| 9 | `chore(structure): create hexagonal architecture package structure` | 1 | .gitkeep files |
| 10 | `feat(database): add Flyway migration for time_deposits table` | 2 | V1__*.sql |
| 11 | `feat(database): add Flyway migration for withdrawals table` | 2 | V2__*.sql |
| 12 | `feat(database): add seed data migration for testing` | 2 | V3__*.sql |
| 13 | `feat(entity): create TimeDepositEntity JPA entity` | 2 | TimeDepositEntity.java |
| 14 | `feat(entity): create WithdrawalEntity JPA entity` | 2 | WithdrawalEntity.java |
| 15 | `feat(repository): create TimeDepositRepository interface` | 2 | Repository files |
| 16 | `feat(repository): create WithdrawalRepository interface` | 2 | Repository files |
| 17 | `test(repository): add integration tests for repositories` | 2 | *IntegrationTest.java |
| 18 | `feat(dto): create WithdrawalDTO for API responses` | 3 | WithdrawalDTO.java |
| 19 | `feat(dto): create TimeDepositResponseDTO for API responses` | 3 | TimeDepositResponseDTO.java |
| 20 | `feat(dto): create UpdateBalancesResponseDTO for API responses` | 3 | UpdateBalancesResponseDTO.java |
| 21 | `feat(mapper): create TimeDepositMapper for entity-DTO conversion` | 3 | TimeDepositMapper.java |
| 22 | `test(mapper): add unit tests for TimeDepositMapper` | 3 | TimeDepositMapperTest.java |
| 23 | `feat(port): create TimeDepositService input port interface` | 3 | TimeDepositServicePort.java |
| 24 | `feat(service): implement TimeDepositServiceImpl` | 3 | TimeDepositServiceImpl.java |
| 25 | `refactor(calculator): make TimeDepositCalculator a Spring bean` | 3 | TimeDepositCalculator.java |
| 26 | `test(service): add unit tests for TimeDepositServiceImpl` | 3 | *ServiceImplTest.java |
| 27 | `feat(config): add OpenAPI/Swagger configuration` | 4 | OpenApiConfig.java |
| 28 | `feat(api): implement GET /api/v1/time-deposits endpoint` | 4 | TimeDepositController.java |
| 29 | `feat(api): implement POST /api/v1/time-deposits/update-balances endpoint` | 4 | TimeDepositController.java |
| 30 | `test(api): add integration tests for TimeDepositController` | 4 | *ControllerIntegrationTest.java |
| 31 | `test(calculator): enhance existing calculator tests for regression` | 4 | TimeDepositCalculatorTest.java |
| 32 | `chore(security): add OWASP Dependency-Check plugin` | 5 | pom.xml, owasp-suppressions.xml |
| 33 | `chore(security): add SpotBugs with FindSecBugs plugin` | 5 | pom.xml, spotbugs-exclude.xml |
| 34 | `chore(security): add JaCoCo for test coverage reporting` | 5 | pom.xml |
| 35 | `chore(docker): create multi-stage Dockerfile` | 6 | Dockerfile |
| 36 | `chore(docker): update docker-compose for full stack deployment` | 6 | docker-compose.yml |
| 37 | `ci: add GitHub Actions CI/CD workflow` | 6 | .github/workflows/ci.yml |
| 38 | `docs: update README with setup and usage instructions` | 6 | README.md |
| 39 | `chore: add .gitignore for Java/Maven/IDE files` | 6 | .gitignore |

---

## Appendix: Key Files Reference

### Files NOT to Modify

These files must remain unchanged (except for adding @Component annotation to TimeDepositCalculator):

1. **`src/main/java/org/ikigaidigital/TimeDeposit.java`**
   - Do NOT modify any code
   - Do NOT change class signature
   - Do NOT add annotations

2. **`src/main/java/org/ikigaidigital/TimeDepositCalculator.java`**
   - Do NOT change method signature of `updateBalance(List<TimeDeposit> xs)`
   - Do NOT modify the calculation logic
   - CAN add `@Component` annotation for Spring DI

### New Files Summary

**Java Classes (16):**
- TimeDepositApplication.java
- TimeDepositEntity.java
- WithdrawalEntity.java
- TimeDepositRepositoryPort.java
- WithdrawalRepositoryPort.java
- JpaTimeDepositRepository.java
- JpaWithdrawalRepository.java
- WithdrawalDTO.java
- TimeDepositResponseDTO.java
- UpdateBalancesResponseDTO.java
- TimeDepositMapper.java
- TimeDepositServicePort.java
- TimeDepositServiceImpl.java
- OpenApiConfig.java
- TimeDepositController.java

**Test Classes (5):**
- TimeDepositRepositoryIntegrationTest.java
- TimeDepositMapperTest.java
- TimeDepositServiceImplTest.java
- TimeDepositControllerIntegrationTest.java
- (Updated) TimeDepositCalculatorTest.java

**Configuration Files (9):**
- application.yml
- application-local.yml
- application-docker.yml
- V1__create_time_deposits_table.sql
- V2__create_withdrawals_table.sql
- V3__seed_sample_data.sql
- owasp-suppressions.xml
- spotbugs-exclude.xml
- .gitignore

**Docker/CI Files (4):**
- docker-compose.yml
- Dockerfile
- .github/workflows/ci.yml
- (Updated) pom.xml

