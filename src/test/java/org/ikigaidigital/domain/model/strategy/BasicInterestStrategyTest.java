package org.ikigaidigital.domain.model.strategy;

import org.ikigaidigital.domain.model.PlanType;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Unit tests for BasicInterestStrategy.
 * 
 * Business Rules:
 * - 1% annual interest rate (0.01 / 12 = ~0.000833 monthly)
 * - Interest accrues only after 30-day grace period
 */
@DisplayName("BasicInterestStrategy Tests")
class BasicInterestStrategyTest {

    private BasicInterestStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new BasicInterestStrategy();
    }

    @Nested
    @DisplayName("supports() method")
    class SupportsMethod {

        @Test
        @DisplayName("returns true for BASIC plan type")
        void returnsTrue_forBasicPlanType() {
            assertThat(strategy.supports(PlanType.BASIC)).isTrue();
        }

        @Test
        @DisplayName("returns false for STUDENT plan type")
        void returnsFalse_forStudentPlanType() {
            assertThat(strategy.supports(PlanType.STUDENT)).isFalse();
        }

        @Test
        @DisplayName("returns false for PREMIUM plan type")
        void returnsFalse_forPremiumPlanType() {
            assertThat(strategy.supports(PlanType.PREMIUM)).isFalse();
        }

        @Test
        @DisplayName("returns false for null plan type")
        void returnsFalse_forNullPlanType() {
            assertThat(strategy.supports(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("Grace Period (≤30 days)")
    class GracePeriod {

        @ParameterizedTest(name = "returns 0 interest at {0} days")
        @ValueSource(ints = {0, 1, 10, 15, 29, 30})
        @DisplayName("returns 0 interest during grace period")
        void returnsZeroInterest_duringGracePeriod(int days) {
            TimeDeposit deposit = new TimeDeposit(1, PlanType.BASIC, 10000.00, days);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isEqualTo(0.0);
        }

        @Test
        @DisplayName("returns 0 interest at exactly 30 days with large balance")
        void returnsZeroInterest_atExactly30Days_withLargeBalance() {
            TimeDeposit deposit = new TimeDeposit(1, PlanType.BASIC, 1000000.00, 30);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("Interest Calculation (>30 days)")
    class InterestCalculation {

        @Test
        @DisplayName("earns interest at 31 days (just after grace period)")
        void earnsInterest_at31Days() {
            // 10000 * 0.01 / 12 = 8.333...
            TimeDeposit deposit = new TimeDeposit(1, PlanType.BASIC, 10000.00, 31);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isCloseTo(8.33, within(0.01));
        }

        @Test
        @DisplayName("calculates correct monthly interest for standard balance")
        void calculatesCorrectInterest_forStandardBalance() {
            // 10000 * 0.01 / 12 = 8.333...
            TimeDeposit deposit = new TimeDeposit(1, PlanType.BASIC, 10000.00, 45);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isCloseTo(8.33, within(0.01));
        }

        @Test
        @DisplayName("calculates correct interest for large balance")
        void calculatesCorrectInterest_forLargeBalance() {
            // 1234567 * 0.01 / 12 = 1028.8058...
            TimeDeposit deposit = new TimeDeposit(1, PlanType.BASIC, 1234567.00, 100);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isCloseTo(1028.81, within(0.01));
        }

        @Test
        @DisplayName("calculates correct interest for small balance")
        void calculatesCorrectInterest_forSmallBalance() {
            // 100 * 0.01 / 12 = 0.0833...
            TimeDeposit deposit = new TimeDeposit(1, PlanType.BASIC, 100.00, 60);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isCloseTo(0.083, within(0.001));
        }

        @Test
        @DisplayName("returns 0 interest for zero balance")
        void returnsZeroInterest_forZeroBalance() {
            TimeDeposit deposit = new TimeDeposit(1, PlanType.BASIC, 0.0, 45);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isEqualTo(0.0);
        }

        @Test
        @DisplayName("continues to earn interest at very long durations")
        void earnsInterest_atLongDuration() {
            // 10000 * 0.01 / 12 = 8.333...
            TimeDeposit deposit = new TimeDeposit(1, PlanType.BASIC, 10000.00, 1000);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isCloseTo(8.33, within(0.01));
        }
    }
}

