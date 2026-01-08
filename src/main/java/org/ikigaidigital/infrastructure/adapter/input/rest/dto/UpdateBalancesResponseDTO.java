package org.ikigaidigital.infrastructure.adapter.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO representing the response after updating time deposit balances.
 */
@Schema(description = "Response after updating time deposit balances with interest")
public record UpdateBalancesResponseDTO(
        @Schema(description = "Status message", example = "Balances updated successfully")
        String message,

        @Schema(description = "Number of deposits updated", example = "3")
        int updatedCount,

        @Schema(description = "Timestamp of the update operation", example = "2024-01-15T10:30:00")
        LocalDateTime timestamp
) {
}

