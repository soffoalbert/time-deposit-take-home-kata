package org.ikigaidigital.infrastructure.config;

import org.ikigaidigital.TimeDepositCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for domain layer beans.
 * 
 * This keeps the domain layer clean of framework annotations
 * while allowing Spring dependency injection.
 */
@Configuration
public class DomainBeanConfig {

    /**
     * Create the TimeDepositCalculator bean.
     * This allows the domain class to remain framework-agnostic.
     *
     * @return the TimeDepositCalculator instance
     */
    @Bean
    public TimeDepositCalculator timeDepositCalculator() {
        return new TimeDepositCalculator();
    }
}

