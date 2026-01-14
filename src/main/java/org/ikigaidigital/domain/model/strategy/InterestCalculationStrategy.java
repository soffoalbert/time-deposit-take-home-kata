package org.ikigaidigital.domain.model.strategy;

import org.ikigaidigital.domain.model.PlanType;
import org.ikigaidigital.domain.model.TimeDeposit;

/**
 * Domain interface for interest calculation strategies.
 * Each plan type has its own strategy implementation.
 *
 * This interface defines the contract for how interest should be calculated
 * as a core business rule. It is NOT an output port (infrastructure concern)
 * but rather a domain policy that encapsulates varying business rules
 * using the Strategy pattern.
 *
 * Implementations:
 * - BasicInterestStrategy: 1% annual rate, 30-day grace period
 * - StudentInterestStrategy: 3% annual rate, 366-day cutoff
 * - PremiumInterestStrategy: 5% annual rate, 45-day minimum
 * - InternalInterestStrategy: 8.5% annual rate, no grace period, terminates at 300 days
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
    boolean supports(PlanType planType);
}

