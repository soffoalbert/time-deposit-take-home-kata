package org.ikigaidigital.application.port.output;

import org.ikigaidigital.domain.model.TimeDeposit;

import java.util.List;

/**
 * Output port interface for time deposit persistence operations.
 * 
 * This port is implemented by infrastructure adapters to provide
 * persistence capabilities without coupling the application layer
 * to specific persistence technologies.
 */
public interface TimeDepositPersistencePort {

    /**
     * Find all time deposits with their associated withdrawals.
     *
     * @return list of time deposit domain objects with withdrawals
     */
    List<TimeDeposit> findAllWithWithdrawals();

    /**
     * Find all time deposits.
     *
     * @return list of time deposit domain objects
     */
    List<TimeDeposit> findAll();

    /**
     * Save all time deposits.
     *
     * @param timeDeposits the time deposits to save
     * @return the saved time deposits
     */
    List<TimeDeposit> saveAll(List<TimeDeposit> timeDeposits);
}

