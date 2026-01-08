package org.ikigaidigital.infrastructure.adapter.input.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ikigaidigital.domain.port.input.TimeDepositServicePort;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.TimeDepositResponseDTO;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.UpdateBalancesResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for Time Deposit operations.
 * Provides endpoints for retrieving time deposits and updating balances.
 */
@RestController
@RequestMapping("/api/v1/time-deposits")
@Tag(name = "Time Deposits", description = "Time deposit management operations")
public class TimeDepositController {

    private final TimeDepositServicePort timeDepositService;

    public TimeDepositController(TimeDepositServicePort timeDepositService) {
        this.timeDepositService = timeDepositService;
    }

    /**
     * Retrieve all time deposits with their associated withdrawals.
     *
     * @return list of time deposit response DTOs
     */
    @GetMapping
    @Operation(
            summary = "Get all time deposits",
            description = "Retrieves all time deposit accounts with their current balances and withdrawal history"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved time deposits",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TimeDepositResponseDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<List<TimeDepositResponseDTO>> getAllTimeDeposits() {
        List<TimeDepositResponseDTO> deposits = timeDepositService.getAllTimeDeposits();
        return ResponseEntity.ok(deposits);
    }

    /**
     * Update balances for all time deposits by applying interest calculations.
     *
     * @return response with update status and count
     */
    @PostMapping("/update-balances")
    @Operation(
            summary = "Update all time deposit balances",
            description = "Applies interest calculations to all time deposits based on their plan type and age"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully updated balances",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateBalancesResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<UpdateBalancesResponseDTO> updateAllBalances() {
        UpdateBalancesResponseDTO response = timeDepositService.updateAllBalances();
        return ResponseEntity.ok(response);
    }
}

