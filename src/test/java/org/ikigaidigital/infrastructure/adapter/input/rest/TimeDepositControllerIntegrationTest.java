package org.ikigaidigital.infrastructure.adapter.input.rest;

import org.ikigaidigital.domain.port.input.TimeDepositServicePort;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.TimeDepositResponseDTO;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.UpdateBalancesResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TimeDepositController.class)
@DisplayName("TimeDepositController Integration Tests")
class TimeDepositControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TimeDepositServicePort timeDepositService;

    @Test
    @DisplayName("GET /api/v1/time-deposits returns list of deposits")
    void getAllTimeDeposits_returnsListOfDeposits() throws Exception {
        // Given
        TimeDepositResponseDTO deposit = new TimeDepositResponseDTO(
                1, "basic", BigDecimal.valueOf(10000.00), 45, Collections.emptyList()
        );
        when(timeDepositService.getAllTimeDeposits()).thenReturn(List.of(deposit));

        // When & Then
        mockMvc.perform(get("/api/v1/time-deposits")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].planType", is("basic")))
                .andExpect(jsonPath("$[0].balance", is(10000.00)))
                .andExpect(jsonPath("$[0].days", is(45)))
                .andExpect(jsonPath("$[0].withdrawals", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/v1/time-deposits returns empty list when no deposits")
    void getAllTimeDeposits_returnsEmptyList() throws Exception {
        // Given
        when(timeDepositService.getAllTimeDeposits()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/v1/time-deposits")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/v1/time-deposits/update-balances returns update response")
    void updateAllBalances_returnsUpdateResponse() throws Exception {
        // Given
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        UpdateBalancesResponseDTO response = new UpdateBalancesResponseDTO(
                "Balances updated successfully", 3, timestamp
        );
        when(timeDepositService.updateAllBalances()).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/time-deposits/update-balances")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Balances updated successfully")))
                .andExpect(jsonPath("$.updatedCount", is(3)))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/time-deposits/update-balances returns zero count for empty list")
    void updateAllBalances_returnsZeroCountForEmptyList() throws Exception {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        UpdateBalancesResponseDTO response = new UpdateBalancesResponseDTO(
                "Balances updated successfully", 0, timestamp
        );
        when(timeDepositService.updateAllBalances()).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/time-deposits/update-balances")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedCount", is(0)));
    }
}

