package org.ikigaidigital.infrastructure.adapter.input.rest.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import org.ikigaidigital.domain.model.PlanType;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO representing a time deposit with its withdrawals in API responses.
 */
@Schema(description = "Time deposit account information with associated withdrawals")
public record TimeDepositResponseDTO(
        @Schema(description = "Unique identifier of the time deposit", example = "1")
        Integer id,

        @Schema(description = "Type of plan (basic, student, premium)", example = "basic")
        PlanType planType,

        @Schema(description = "Current balance of the deposit", example = "10000.00")
        BigDecimal balance,

        @Schema(description = "Number of days the deposit has been active", example = "45")
        Integer days,

        @Schema(description = "List of withdrawals made from this deposit")
        List<WithdrawalDTO> withdrawals
) {
}

