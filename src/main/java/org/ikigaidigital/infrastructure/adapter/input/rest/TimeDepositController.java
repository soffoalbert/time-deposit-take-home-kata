package org.ikigaidigital.infrastructure.adapter.input.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ikigaidigital.application.port.input.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.input.UpdateAllBalancesUseCase;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.TimeDepositResponseDTO;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.UpdateBalancesResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Time Deposit operations.
 *
 * This is an input adapter in the hexagonal architecture that:
 * - Receives HTTP requests
 * - Delegates to application layer use cases
 * - Maps domain objects to DTOs for API responses
 */
@RestController
@RequestMapping("/api/v1/time-deposits")
@Tag(name = "Time Deposits", description = "Time deposit management operations")
public class TimeDepositController {

    private final GetAllTimeDepositsUseCase getAllTimeDepositsUseCase;
    private final UpdateAllBalancesUseCase updateAllBalancesUseCase;

    public TimeDepositController(
            GetAllTimeDepositsUseCase getAllTimeDepositsUseCase,
            UpdateAllBalancesUseCase updateAllBalancesUseCase) {
        this.getAllTimeDepositsUseCase = getAllTimeDepositsUseCase;
        this.updateAllBalancesUseCase = updateAllBalancesUseCase;
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
        List<TimeDeposit> deposits = getAllTimeDepositsUseCase.getAllTimeDeposits();
        List<TimeDepositResponseDTO> response = deposits.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
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
        UpdateAllBalancesUseCase.UpdateBalancesResult result = updateAllBalancesUseCase.updateAllBalances();
        UpdateBalancesResponseDTO response = new UpdateBalancesResponseDTO(
                "Balances updated successfully",
                result.updatedCount(),
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Convert a domain TimeDeposit to a response DTO.
     * This mapping is an infrastructure concern and belongs in the adapter.
     */
    private TimeDepositResponseDTO toDTO(TimeDeposit domain) {
        return new TimeDepositResponseDTO(
                domain.getId(),
                domain.getPlanType(),
                BigDecimal.valueOf(domain.getBalance()),
                domain.getDays(),
                Collections.emptyList() // Withdrawals not included in current domain model
        );
    }
}

