package org.ikigaidigital.infrastructure.config;

import org.ikigaidigital.domain.model.InterestStrategyFactory;
import org.ikigaidigital.domain.model.TimeDepositCalculator;
import org.ikigaidigital.domain.model.strategy.BasicInterestStrategy;
import org.ikigaidigital.domain.model.strategy.PremiumInterestStrategy;
import org.ikigaidigital.domain.model.strategy.StudentInterestStrategy;
import org.ikigaidigital.domain.model.strategy.InterestCalculationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Spring configuration for domain layer beans.
 *
 * This keeps the domain layer clean of framework annotations
 * while allowing Spring dependency injection.
 *
 * Beans are wired following Hexagonal Architecture principles:
 * - Strategy implementations are created as beans
 * - Factory aggregates all strategies
 * - Calculator uses the factory for interest calculations
 */
@Configuration
public class DomainBeanConfig {

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

