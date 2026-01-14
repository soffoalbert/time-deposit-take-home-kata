package org.ikigaidigital.domain.model.strategy;

import org.ikigaidigital.domain.model.TimeDeposit;


/**
 * Interest calculation strategy for Internal plan deposits.
 * 
 * Business Rules:
 * - 8.5% annual interest rate
 * - Interest accrues from day 1 (no grace period)
 * - Plan automatically terminates at exactly 300 days
 * - Balance is reset to 0 when plan terminates at day 300
 * 
 * This is a pure domain class with no framework dependencies.
 */
public class InternalInterestStrategy implements InterestCalculationStrategy {

    private static final String PLAN_TYPE = "internal";
    private static final int TERMINATION_DAY = 300;
    private static final double ANNUAL_INTEREST_RATE = 0.085;
    private static final int MONTHS_PER_YEAR = 12;

    @Override
    public double calculateInterest(TimeDeposit deposit) {
        // Plan terminates at day 300 - balance becomes 0, so no interest
        if (deposit.getDays() >= TERMINATION_DAY) {
            return 0.0;
        }
        
        // Interest accrues from day 1 (no grace period)
        return deposit.getBalance() * ANNUAL_INTEREST_RATE / MONTHS_PER_YEAR;
    }

    @Override
    public boolean supports(String planType) {
        return PLAN_TYPE.equals(planType);
    }
}


