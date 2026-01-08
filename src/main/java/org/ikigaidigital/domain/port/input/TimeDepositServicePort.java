package org.ikigaidigital.domain.port.input;

import org.ikigaidigital.infrastructure.adapter.input.rest.dto.TimeDepositResponseDTO;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.UpdateBalancesResponseDTO;

import java.util.List;

/**
 * Input port interface for TimeDeposit use cases.
 * Defines the contract for time deposit operations from the application's perspective.
 */
public interface TimeDepositServicePort {

    /**
     * Retrieve all time deposits with their associated withdrawals.
     *
     * @return list of time deposit response DTOs
     */
    List<TimeDepositResponseDTO> getAllTimeDeposits();

    /**
     * Update balances for all time deposits by applying interest calculations.
     * Uses the existing TimeDepositCalculator logic.
     *
     * @return response DTO with update status and count
     */
    UpdateBalancesResponseDTO updateAllBalances();
}

