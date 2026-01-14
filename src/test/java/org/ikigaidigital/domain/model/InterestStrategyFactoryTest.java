package org.ikigaidigital.domain.model;

import org.ikigaidigital.domain.model.strategy.BasicInterestStrategy;
import org.ikigaidigital.domain.model.strategy.InternalInterestStrategy;
import org.ikigaidigital.domain.model.strategy.PremiumInterestStrategy;
import org.ikigaidigital.domain.model.strategy.StudentInterestStrategy;
import org.ikigaidigital.domain.model.strategy.InterestCalculationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Unit tests for InterestStrategyFactory.
 *
 * Tests strategy selection, delegation, and edge cases.
 */
@DisplayName("InterestStrategyFactory Tests")
class InterestStrategyFactoryTest {

    private InterestStrategyFactory factory;
    private BasicInterestStrategy basicStrategy;
    private StudentInterestStrategy studentStrategy;
    private PremiumInterestStrategy premiumStrategy;
    private InternalInterestStrategy internalStrategy;

    @BeforeEach
    void setUp() {
        basicStrategy = new BasicInterestStrategy();
        studentStrategy = new StudentInterestStrategy();
        premiumStrategy = new PremiumInterestStrategy();
        internalStrategy = new InternalInterestStrategy();
        factory = new InterestStrategyFactory(List.of(basicStrategy, studentStrategy, premiumStrategy, internalStrategy));
    }

    @Nested
    @DisplayName("getStrategy() method")
    class GetStrategy {

        @Test
        @DisplayName("returns BasicInterestStrategy for BASIC plan type")
        void returnsBasicStrategy_forBasicPlanType() {
            Optional<InterestCalculationStrategy> result = factory.getStrategy(PlanType.BASIC);

            assertThat(result).isPresent();
            assertThat(result.get()).isInstanceOf(BasicInterestStrategy.class);
        }

        @Test
        @DisplayName("returns StudentInterestStrategy for STUDENT plan type")
        void returnsStudentStrategy_forStudentPlanType() {
            Optional<InterestCalculationStrategy> result = factory.getStrategy(PlanType.STUDENT);

            assertThat(result).isPresent();
            assertThat(result.get()).isInstanceOf(StudentInterestStrategy.class);
        }

        @Test
        @DisplayName("returns PremiumInterestStrategy for PREMIUM plan type")
        void returnsPremiumStrategy_forPremiumPlanType() {
            Optional<InterestCalculationStrategy> result = factory.getStrategy(PlanType.PREMIUM);

            assertThat(result).isPresent();
            assertThat(result.get()).isInstanceOf(PremiumInterestStrategy.class);
        }

        @Test
        @DisplayName("returns empty Optional for null plan type")
        void returnsEmpty_forNullPlanType() {
            Optional<InterestCalculationStrategy> result = factory.getStrategy(null);

            assertThat(result).isEmpty();
        }


    }

    @Nested
    @DisplayName("calculateInterest() method")
    class CalculateInterest {

        @Test
        @DisplayName("delegates to BasicInterestStrategy for basic plan")
        void delegatesToBasicStrategy() {
            TimeDeposit deposit = new TimeDeposit(1, PlanType.BASIC, 10000.00, 45);

            double interest = factory.calculateInterest(deposit);

            // 10000 * 0.01 / 12 = 8.333...
            assertThat(interest).isCloseTo(8.33, within(0.01));
        }

        @Test
        @DisplayName("delegates to StudentInterestStrategy for student plan")
        void delegatesToStudentStrategy() {
            TimeDeposit deposit = new TimeDeposit(1, PlanType.STUDENT, 5000.00, 100);

            double interest = factory.calculateInterest(deposit);

            // 5000 * 0.03 / 12 = 12.50
            assertThat(interest).isCloseTo(12.50, within(0.01));
        }

        @Test
        @DisplayName("delegates to PremiumInterestStrategy for premium plan")
        void delegatesToPremiumStrategy() {
            TimeDeposit deposit = new TimeDeposit(1, PlanType.PREMIUM, 50000.00, 60);

            double interest = factory.calculateInterest(deposit);

            // 50000 * 0.05 / 12 = 208.333...
            assertThat(interest).isCloseTo(208.33, within(0.01));
        }

        @Test
        @DisplayName("delegates to InternalInterestStrategy for internal plan")
        void delegatesToInternalStrategy() {
            TimeDeposit deposit = new TimeDeposit(1, PlanType.INTERNAL, 10000.00, 100);

            double interest = factory.calculateInterest(deposit);

            // 10000 * 0.085 / 12 = 70.833...
            assertThat(interest).isCloseTo(70.83, within(0.01));
        }



        @Test
        @DisplayName("returns 0.0 for null plan type")
        void returnsZero_forNullPlanType() {
            TimeDeposit deposit = new TimeDeposit(1, null, 10000.00, 45);

            double interest = factory.calculateInterest(deposit);

            assertThat(interest).isEqualTo(0.0);
        }

        @Test
        @DisplayName("respects grace period rules via strategy delegation")
        void respectsGracePeriod_viaStrategyDelegation() {
            TimeDeposit basicAt30Days = new TimeDeposit(1, PlanType.BASIC, 10000.00, 30);
            TimeDeposit studentAt30Days = new TimeDeposit(2, PlanType.STUDENT, 5000.00, 30);
            TimeDeposit premiumAt30Days = new TimeDeposit(3, PlanType.PREMIUM, 50000.00, 30);

            assertThat(factory.calculateInterest(basicAt30Days)).isEqualTo(0.0);
            assertThat(factory.calculateInterest(studentAt30Days)).isEqualTo(0.0);
            assertThat(factory.calculateInterest(premiumAt30Days)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("respects student 366-day cutoff via strategy delegation")
        void respectsStudentCutoff_viaStrategyDelegation() {
            TimeDeposit at365Days = new TimeDeposit(1, PlanType.STUDENT, 5000.00, 365);
            TimeDeposit at366Days = new TimeDeposit(2, PlanType.STUDENT, 5000.00, 366);

            assertThat(factory.calculateInterest(at365Days)).isGreaterThan(0.0);
            assertThat(factory.calculateInterest(at366Days)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("respects premium 45-day minimum via strategy delegation")
        void respectsPremiumMinimum_viaStrategyDelegation() {
            TimeDeposit at45Days = new TimeDeposit(1, PlanType.PREMIUM, 50000.00, 45);
            TimeDeposit at46Days = new TimeDeposit(2, PlanType.PREMIUM, 50000.00, 46);

            assertThat(factory.calculateInterest(at45Days)).isEqualTo(0.0);
            assertThat(factory.calculateInterest(at46Days)).isGreaterThan(0.0);
        }
    }

    @Nested
    @DisplayName("Factory with empty strategies list")
    class EmptyStrategies {

        @Test
        @DisplayName("getStrategy returns empty for any plan type when no strategies configured")
        void getStrategy_returnsEmpty_whenNoStrategies() {
            InterestStrategyFactory emptyFactory = new InterestStrategyFactory(Collections.emptyList());

            assertThat(emptyFactory.getStrategy(PlanType.BASIC)).isEmpty();
            assertThat(emptyFactory.getStrategy(PlanType.STUDENT)).isEmpty();
            assertThat(emptyFactory.getStrategy(PlanType.PREMIUM)).isEmpty();
        }

        @Test
        @DisplayName("calculateInterest returns 0.0 for any plan type when no strategies configured")
        void calculateInterest_returnsZero_whenNoStrategies() {
            InterestStrategyFactory emptyFactory = new InterestStrategyFactory(Collections.emptyList());
            TimeDeposit deposit = new TimeDeposit(1, PlanType.BASIC, 10000.00, 45);

            double interest = emptyFactory.calculateInterest(deposit);

            assertThat(interest).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("Factory with subset of strategies")
    class PartialStrategies {

        @Test
        @DisplayName("only returns strategies that are configured")
        void onlyReturnsConfiguredStrategies() {
            InterestStrategyFactory partialFactory = new InterestStrategyFactory(
                    List.of(new BasicInterestStrategy())
            );

            assertThat(partialFactory.getStrategy(PlanType.BASIC)).isPresent();
            assertThat(partialFactory.getStrategy(PlanType.STUDENT)).isEmpty();
            assertThat(partialFactory.getStrategy(PlanType.PREMIUM)).isEmpty();
        }

        @Test
        @DisplayName("calculates interest only for configured plan types")
        void calculatesInterest_onlyForConfiguredPlanTypes() {
            InterestStrategyFactory partialFactory = new InterestStrategyFactory(
                    List.of(new BasicInterestStrategy())
            );

            TimeDeposit basicDeposit = new TimeDeposit(1, PlanType.BASIC, 10000.00, 45);
            TimeDeposit studentDeposit = new TimeDeposit(2, PlanType.STUDENT, 5000.00, 100);

            assertThat(partialFactory.calculateInterest(basicDeposit)).isGreaterThan(0.0);
            assertThat(partialFactory.calculateInterest(studentDeposit)).isEqualTo(0.0);
        }
    }
}

