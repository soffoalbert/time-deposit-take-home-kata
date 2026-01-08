package org.ikigaidigital.domain.model;

import org.ikigaidigital.domain.model.strategy.BasicInterestStrategy;
import org.ikigaidigital.domain.model.strategy.PremiumInterestStrategy;
import org.ikigaidigital.domain.model.strategy.StudentInterestStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Domain service for calculating time deposit interest.
 * Contains the core business logic for interest calculation.
 *
 * Business Rules:
 * - No interest for the first 30 days (grace period)
 * - Student plan: 3% annual rate, only for deposits less than 366 days
 * - Premium plan: 5% annual rate, only after 45 days
 * - Basic plan: 1% annual rate
 *
 * This class uses the Strategy pattern to delegate interest calculations
 * to plan-specific strategy implementations, enabling:
 * - Easy addition of new plan types without modifying this class
 * - Independent testing of each plan's calculation logic
 * - Clear separation of business rules per plan type
 *
 * This is a pure domain class with no framework dependencies.
 * Spring configuration is done via @Bean in infrastructure layer.
 */
public class TimeDepositCalculator {

    private final InterestStrategyFactory strategyFactory;

    /**
     * Create a TimeDepositCalculator with the given strategy factory.
     *
     * @param strategyFactory factory for resolving interest calculation strategies
     */
    public TimeDepositCalculator(InterestStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    /**
     * Default constructor for backward compatibility.
     * Creates a calculator with all default strategies.
     */
    public TimeDepositCalculator() {
        this(new InterestStrategyFactory(List.of(
                new BasicInterestStrategy(),
                new StudentInterestStrategy(),
                new PremiumInterestStrategy()
        )));
    }

    /**
     * Updates the balance of all time deposits by applying monthly interest.
     *
     * @param xs the list of time deposits to update
     */
    public void updateBalance(List<TimeDeposit> xs) {
        for (TimeDeposit deposit : xs) {
            double interest = strategyFactory.calculateInterest(deposit);
            double roundedInterest = new BigDecimal(interest)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            deposit.setBalance(deposit.getBalance() + roundedInterest);
        }
    }
}

