package org.ikigaidigital.infrastructure.adapter.input.rest;

import org.ikigaidigital.application.port.input.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.input.UpdateAllBalancesUseCase;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    private GetAllTimeDepositsUseCase getAllTimeDepositsUseCase;

    @MockBean
    private UpdateAllBalancesUseCase updateAllBalancesUseCase;

    @Test
    @DisplayName("GET /api/v1/time-deposits returns list of deposits")
    void getAllTimeDeposits_returnsListOfDeposits() throws Exception {
        // Given
        TimeDeposit deposit = new TimeDeposit(1, "basic", 10000.00, 45);
        when(getAllTimeDepositsUseCase.getAllTimeDeposits()).thenReturn(List.of(deposit));

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
        when(getAllTimeDepositsUseCase.getAllTimeDeposits()).thenReturn(Collections.emptyList());

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
        UpdateAllBalancesUseCase.UpdateBalancesResult result =
                new UpdateAllBalancesUseCase.UpdateBalancesResult(3);
        when(updateAllBalancesUseCase.updateAllBalances()).thenReturn(result);

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
        UpdateAllBalancesUseCase.UpdateBalancesResult result =
                new UpdateAllBalancesUseCase.UpdateBalancesResult(0);
        when(updateAllBalancesUseCase.updateAllBalances()).thenReturn(result);

        // When & Then
        mockMvc.perform(post("/api/v1/time-deposits/update-balances")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedCount", is(0)));
    }
}

