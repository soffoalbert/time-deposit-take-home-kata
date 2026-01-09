package org.ikigaidigital.application.service;

import org.ikigaidigital.application.port.input.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.input.UpdateAllBalancesUseCase;
import org.ikigaidigital.application.port.output.TimeDepositPersistencePort;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.TimeDepositCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service that orchestrates time deposit use cases.
 * 
 * This service implements the input ports (use cases) and coordinates
 * between the domain layer and output ports (infrastructure adapters).
 * It contains no business logic - that belongs in the domain layer.
 */
@Service
public class TimeDepositApplicationService implements GetAllTimeDepositsUseCase, UpdateAllBalancesUseCase {

    private final TimeDepositPersistencePort timeDepositPersistencePort;
    private final TimeDepositCalculator timeDepositCalculator;

    public TimeDepositApplicationService(
            TimeDepositPersistencePort timeDepositPersistencePort,
            TimeDepositCalculator timeDepositCalculator) {
        this.timeDepositPersistencePort = timeDepositPersistencePort;
        this.timeDepositCalculator = timeDepositCalculator;
    }

    /**
     * Get all time deposits with their withdrawal information.
     *
     * @return list of time deposit domain objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<TimeDeposit> getAllTimeDeposits() {
        return timeDepositPersistencePort.findAllWithWithdrawals();
    }

    /**
     * Update all time deposit balances by applying interest calculations.
     *
     * @return result containing the count of updated deposits
     */
    @Override
    @Transactional
    public UpdateBalancesResult updateAllBalances() {
        // 1. Fetch all deposits as domain objects
        List<TimeDeposit> timeDeposits = timeDepositPersistencePort.findAll();

        // 2. Apply interest calculations via domain service
        timeDepositCalculator.updateBalance(timeDeposits);

        // 3. Persist updated deposits
        timeDepositPersistencePort.saveAll(timeDeposits);

        // 4. Return result
        return new UpdateBalancesResult(timeDeposits.size());
    }
}

