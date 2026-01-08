package org.ikigaidigital.domain.model.strategy;

import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.port.output.InterestCalculationStrategy;

/**
 * Interest calculation strategy for Premium plan deposits.
 * 
 * Business Rules:
 * - 5% annual interest rate
 * - Interest accrues only after 45 days (not 30-day grace period)
 * 
 * This is a pure domain class with no framework dependencies.
 */
public class PremiumInterestStrategy implements InterestCalculationStrategy {

    private static final String PLAN_TYPE = "premium";
    private static final int GRACE_PERIOD_DAYS = 30;
    private static final int PREMIUM_MINIMUM_DAYS = 45;
    private static final double ANNUAL_INTEREST_RATE = 0.05;
    private static final int MONTHS_PER_YEAR = 12;

    @Override
    public double calculateInterest(TimeDeposit deposit) {
        // Premium must pass both: general 30-day grace period AND 45-day minimum
        if (deposit.getDays() <= GRACE_PERIOD_DAYS) {
            return 0.0;
        }
        if (deposit.getDays() <= PREMIUM_MINIMUM_DAYS) {
            return 0.0;
        }
        return deposit.getBalance() * ANNUAL_INTEREST_RATE / MONTHS_PER_YEAR;
    }

    @Override
    public boolean supports(String planType) {
        return PLAN_TYPE.equals(planType);
    }
}

