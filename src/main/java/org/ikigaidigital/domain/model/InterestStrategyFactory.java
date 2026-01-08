package org.ikigaidigital.domain.model;

import org.ikigaidigital.domain.port.output.InterestCalculationStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Factory for resolving the appropriate interest calculation strategy
 * based on the plan type.
 * 
 * This is a pure domain class with no framework dependencies.
 * Strategy implementations are injected via constructor.
 */
public class InterestStrategyFactory {

    private final List<InterestCalculationStrategy> strategies;

    /**
     * Create a factory with the available strategies.
     * 
     * @param strategies list of all available interest calculation strategies
     */
    public InterestStrategyFactory(List<InterestCalculationStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * Get the appropriate strategy for a given plan type.
     * 
     * @param planType the plan type to find a strategy for
     * @return Optional containing the strategy if found, empty otherwise
     */
    public Optional<InterestCalculationStrategy> getStrategy(String planType) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(planType))
                .findFirst();
    }

    /**
     * Calculate interest for a time deposit using the appropriate strategy.
     * Returns 0.0 if no strategy is found for the plan type.
     * 
     * @param deposit the time deposit to calculate interest for
     * @return the calculated interest amount
     */
    public double calculateInterest(TimeDeposit deposit) {
        return getStrategy(deposit.getPlanType())
                .map(strategy -> strategy.calculateInterest(deposit))
                .orElse(0.0);
    }
}

