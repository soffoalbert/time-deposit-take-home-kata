package org.ikigaidigital.application.port.input;

import org.ikigaidigital.domain.model.TimeDeposit;

import java.util.List;

/**
 * Use case interface for retrieving all time deposits.
 *
 * This is an application layer input port that defines the contract
 * for the "get all time deposits" use case without any infrastructure concerns.
 */
public interface GetAllTimeDepositsUseCase {

    /**
     * Get all time deposits with their withdrawal information.
     *
     * @return list of time deposit domain objects
     */
    List<TimeDeposit> getAllTimeDeposits();
}

