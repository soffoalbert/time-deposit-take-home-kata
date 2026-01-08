package org.ikigaidigital.domain.port.output;

import org.ikigaidigital.domain.model.TimeDeposit;

/**
 * Output port interface for interest calculation strategies.
 * Each plan type has its own strategy implementation.
 * 
 * This is part of the Hexagonal Architecture - defining the contract
 * for how interest should be calculated without specifying implementation details.
 */
public interface InterestCalculationStrategy {

    /**
     * Calculate the monthly interest for a time deposit.
     * 
     * @param deposit the time deposit to calculate interest for
     * @return the calculated interest amount (not yet applied to balance)
     */
    double calculateInterest(TimeDeposit deposit);

    /**
     * Check if this strategy supports the given plan type.
     * 
     * @param planType the plan type to check
     * @return true if this strategy handles the given plan type
     */
    boolean supports(String planType);
}

