package org.ikigaidigital.domain.model.strategy;

import org.ikigaidigital.domain.model.TimeDeposit;


/**
 * Interest calculation strategy for Student plan deposits.
 * 
 * Business Rules:
 * - 3% annual interest rate
 * - Interest accrues only after 30-day grace period
 * - Interest only applies for deposits less than 366 days old
 * 
 * This is a pure domain class with no framework dependencies.
 */
public class StudentInterestStrategy implements InterestCalculationStrategy {

    private static final String PLAN_TYPE = "student";
    private static final int GRACE_PERIOD_DAYS = 30;
    private static final int MAX_DAYS_FOR_INTEREST = 366;
    private static final double ANNUAL_INTEREST_RATE = 0.03;
    private static final int MONTHS_PER_YEAR = 12;

    @Override
    public double calculateInterest(TimeDeposit deposit) {
        if (deposit.getDays() <= GRACE_PERIOD_DAYS) {
            return 0.0;
        }
        if (deposit.getDays() >= MAX_DAYS_FOR_INTEREST) {
            return 0.0;
        }
        return deposit.getBalance() * ANNUAL_INTEREST_RATE / MONTHS_PER_YEAR;
    }

    @Override
    public boolean supports(String planType) {
        return PLAN_TYPE.equals(planType);
    }
}

