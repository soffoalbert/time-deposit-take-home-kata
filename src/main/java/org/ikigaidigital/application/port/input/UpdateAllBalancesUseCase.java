package org.ikigaidigital.application.port.input;

/**
 * Use case interface for updating all time deposit balances.
 *
 * This is an application layer input port that defines the contract
 * for the "update all balances" use case without any infrastructure concerns.
 */
public interface UpdateAllBalancesUseCase {

    /**
     * Result record containing the outcome of the balance update operation.
     *
     * @param updatedCount number of deposits that were updated
     */
    record UpdateBalancesResult(int updatedCount) {}

    /**
     * Update all time deposit balances by applying interest calculations.
     * Interest is calculated based on each deposit's plan type and age.
     *
     * @return result containing the count of updated deposits
     */
    UpdateBalancesResult updateAllBalances();
}

