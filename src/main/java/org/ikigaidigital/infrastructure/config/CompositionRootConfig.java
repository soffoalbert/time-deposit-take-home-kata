package org.ikigaidigital.infrastructure.config;

import org.ikigaidigital.domain.model.InterestStrategyFactory;
import org.ikigaidigital.domain.model.TimeDepositCalculator;
import org.ikigaidigital.domain.model.strategy.BasicInterestStrategy;
import org.ikigaidigital.domain.model.strategy.InternalInterestStrategy;
import org.ikigaidigital.domain.model.strategy.PremiumInterestStrategy;
import org.ikigaidigital.domain.model.strategy.StudentInterestStrategy;
import org.ikigaidigital.domain.model.strategy.InterestCalculationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Composition Root - the single place where the dependency graph is assembled.
 *
 * This configuration class wires domain objects as Spring beans while keeping
 * the domain layer completely framework-agnostic (no Spring annotations in domain classes).
 *
 * This follows Hexagonal Architecture and Clean Architecture principles:
 * - Domain classes remain pure Java with no framework dependencies
 * - Infrastructure layer is responsible for dependency injection wiring
 * - Domain objects can be unit tested without Spring context
 *
 * Beans configured:
 * - Interest calculation strategies (Basic, Student, Premium, Internal)
 * - InterestStrategyFactory (aggregates strategies)
 * - TimeDepositCalculator (uses factory for calculations)
 */
@Configuration
public class CompositionRootConfig {

    /**
     * Create the BasicInterestStrategy bean.
     *
     * @return the BasicInterestStrategy instance
     */
    @Bean
    public InterestCalculationStrategy basicInterestStrategy() {
        return new BasicInterestStrategy();
    }

    /**
     * Create the StudentInterestStrategy bean.
     *
     * @return the StudentInterestStrategy instance
     */
    @Bean
    public InterestCalculationStrategy studentInterestStrategy() {
        return new StudentInterestStrategy();
    }

    /**
     * Create the PremiumInterestStrategy bean.
     *
     * @return the PremiumInterestStrategy instance
     */
    @Bean
    public InterestCalculationStrategy premiumInterestStrategy() {
        return new PremiumInterestStrategy();
    }

    /**
     * Create the InternalInterestStrategy bean.
     *
     * @return the InternalInterestStrategy instance
     */
    @Bean
    public InterestCalculationStrategy internalInterestStrategy() {
        return new InternalInterestStrategy();
    }

    /**
     * Create the InterestStrategyFactory bean.
     * Spring automatically injects all InterestCalculationStrategy beans.
     *
     * @param strategies all available interest calculation strategies
     * @return the InterestStrategyFactory instance
     */
    @Bean
    public InterestStrategyFactory interestStrategyFactory(List<InterestCalculationStrategy> strategies) {
        return new InterestStrategyFactory(strategies);
    }

    /**
     * Create the TimeDepositCalculator bean.
     * This allows the domain class to remain framework-agnostic.
     *
     * @param strategyFactory the strategy factory for interest calculations
     * @return the TimeDepositCalculator instance
     */
    @Bean
    public TimeDepositCalculator timeDepositCalculator(InterestStrategyFactory strategyFactory) {
        return new TimeDepositCalculator(strategyFactory);
    }
}

