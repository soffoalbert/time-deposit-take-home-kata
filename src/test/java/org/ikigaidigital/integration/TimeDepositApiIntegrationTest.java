package org.ikigaidigital.integration;

import org.ikigaidigital.infrastructure.adapter.input.rest.dto.TimeDepositResponseDTO;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.UpdateBalancesResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Full-stack integration tests for the Time Deposit API.
 * Tests the complete flow: database → repository → service → controller → API response.
 * Uses Testcontainers PostgreSQL with Flyway migrations.
 */
@DisplayName("Time Deposit API Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TimeDepositApiIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    @DisplayName("GET /api/v1/time-deposits returns seeded data from migrations")
    void getAllTimeDeposits_returnsSeededData() {
        // When
        ResponseEntity<List<TimeDepositResponseDTO>> response = restTemplate.exchange(
                "/api/v1/time-deposits",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(3); // 3 deposits from V3 seed migration

        // Verify deposit types from seed data
        List<String> planTypes = response.getBody().stream()
                .map(dto -> dto.planType().getValue())
                .toList();
        assertThat(planTypes).containsExactlyInAnyOrder("basic", "student", "premium");
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/v1/time-deposits returns deposits with correct structure")
    void getAllTimeDeposits_returnsCorrectStructure() {
        // When
        ResponseEntity<List<TimeDepositResponseDTO>> response = restTemplate.exchange(
                "/api/v1/time-deposits",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getBody()).isNotNull();
        TimeDepositResponseDTO deposit = response.getBody().get(0);

        assertThat(deposit.id()).isNotNull();
        assertThat(deposit.planType()).isNotNull();
        assertThat(deposit.balance()).isNotNull();
        assertThat(deposit.days()).isNotNull();
        assertThat(deposit.withdrawals()).isNotNull();
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/v1/time-deposits/update-balances updates all deposits")
    void updateAllBalances_updatesDeposits() {
        // When
        ResponseEntity<UpdateBalancesResponseDTO> response = restTemplate.postForEntity(
                "/api/v1/time-deposits/update-balances",
                null,
                UpdateBalancesResponseDTO.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Balances updated successfully");
        assertThat(response.getBody().updatedCount()).isEqualTo(3);
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    @Order(4)
    @DisplayName("Balances are actually updated after POST update-balances")
    void updateAllBalances_actuallyChangesBalances() {
        // Given - Get initial balances
        ResponseEntity<List<TimeDepositResponseDTO>> beforeResponse = restTemplate.exchange(
                "/api/v1/time-deposits",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(beforeResponse.getBody()).isNotNull();

        // Store initial balances
        BigDecimal basicBalanceBefore = beforeResponse.getBody().stream()
                .filter(d -> "basic".equals(d.planType().getValue()))
                .findFirst()
                .map(TimeDepositResponseDTO::balance)
                .orElseThrow();

        // When - Update balances
        restTemplate.postForEntity(
                "/api/v1/time-deposits/update-balances",
                null,
                UpdateBalancesResponseDTO.class
        );

        // Then - Get updated balances
        ResponseEntity<List<TimeDepositResponseDTO>> afterResponse = restTemplate.exchange(
                "/api/v1/time-deposits",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(afterResponse.getBody()).isNotNull();

        BigDecimal basicBalanceAfter = afterResponse.getBody().stream()
                .filter(d -> "basic".equals(d.planType().getValue()))
                .findFirst()
                .map(TimeDepositResponseDTO::balance)
                .orElseThrow();

        // Basic plan with >30 days should have earned interest
        assertThat(basicBalanceAfter).isGreaterThan(basicBalanceBefore);
    }
}

