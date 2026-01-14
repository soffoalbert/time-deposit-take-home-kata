package org.ikigaidigital.domain.model.strategy;

import org.ikigaidigital.domain.model.PlanType;
import org.ikigaidigital.domain.model.TimeDeposit;


/**
 * Interest calculation strategy for Basic plan deposits.
 *
 * Business Rules:
 * - 1% annual interest rate
 * - Interest accrues only after 30-day grace period
 *
 * This is a pure domain class with no framework dependencies.
 */
public class BasicInterestStrategy implements InterestCalculationStrategy {

    private static final PlanType PLAN_TYPE = PlanType.BASIC;
    private static final int GRACE_PERIOD_DAYS = 30;
    private static final double ANNUAL_INTEREST_RATE = 0.01;
    private static final int MONTHS_PER_YEAR = 12;

    @Override
    public double calculateInterest(TimeDeposit deposit) {
        if (deposit.getDays() <= GRACE_PERIOD_DAYS) {
            return 0.0;
        }
        return deposit.getBalance() * ANNUAL_INTEREST_RATE / MONTHS_PER_YEAR;
    }

    @Override
    public boolean supports(PlanType planType) {
        return PLAN_TYPE == planType;
    }
}

