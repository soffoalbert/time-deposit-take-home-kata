package org.ikigaidigital.infrastructure.adapter.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO representing a withdrawal in API responses.
 */
@Schema(description = "Withdrawal information")
public record WithdrawalDTO(
        @Schema(description = "Unique identifier of the withdrawal", example = "1")
        Integer id,

        @Schema(description = "Amount withdrawn", example = "500.00")
        BigDecimal amount,

        @Schema(description = "Date of the withdrawal", example = "2024-01-15")
        LocalDate date
) {
}

