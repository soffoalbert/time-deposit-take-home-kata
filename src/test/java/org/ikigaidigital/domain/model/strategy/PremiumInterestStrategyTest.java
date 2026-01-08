package org.ikigaidigital.domain.model.strategy;

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
 * Unit tests for PremiumInterestStrategy.
 * 
 * Business Rules:
 * - 5% annual interest rate (0.05 / 12 = ~0.00417 monthly)
 * - Interest accrues only after 30-day grace period
 * - Interest starts only after 45 days (additional requirement beyond grace period)
 */
@DisplayName("PremiumInterestStrategy Tests")
class PremiumInterestStrategyTest {

    private PremiumInterestStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new PremiumInterestStrategy();
    }

    @Nested
    @DisplayName("supports() method")
    class SupportsMethod {

        @Test
        @DisplayName("returns true for 'premium' plan type")
        void returnsTrue_forPremiumPlanType() {
            assertThat(strategy.supports("premium")).isTrue();
        }

        @Test
        @DisplayName("returns false for 'basic' plan type")
        void returnsFalse_forBasicPlanType() {
            assertThat(strategy.supports("basic")).isFalse();
        }

        @Test
        @DisplayName("returns false for 'student' plan type")
        void returnsFalse_forStudentPlanType() {
            assertThat(strategy.supports("student")).isFalse();
        }

        @Test
        @DisplayName("returns false for null plan type")
        void returnsFalse_forNullPlanType() {
            assertThat(strategy.supports(null)).isFalse();
        }

        @Test
        @DisplayName("returns false for empty string")
        void returnsFalse_forEmptyString() {
            assertThat(strategy.supports("")).isFalse();
        }

        @Test
        @DisplayName("returns false for 'PREMIUM' (case sensitive)")
        void returnsFalse_forUppercasePremium() {
            assertThat(strategy.supports("PREMIUM")).isFalse();
        }
    }

    @Nested
    @DisplayName("Grace Period (≤30 days)")
    class GracePeriod {

        @ParameterizedTest(name = "returns 0 interest at {0} days")
        @ValueSource(ints = {0, 1, 10, 15, 29, 30})
        @DisplayName("returns 0 interest during grace period")
        void returnsZeroInterest_duringGracePeriod(int days) {
            TimeDeposit deposit = new TimeDeposit(1, "premium", 50000.00, days);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("Premium 45-Day Minimum (31-45 days)")
    class PremiumMinimum {

        @ParameterizedTest(name = "returns 0 interest at {0} days")
        @ValueSource(ints = {31, 35, 40, 44, 45})
        @DisplayName("returns 0 interest between grace period and 45-day minimum")
        void returnsZeroInterest_beforePremiumMinimum(int days) {
            TimeDeposit deposit = new TimeDeposit(1, "premium", 50000.00, days);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isEqualTo(0.0);
        }

        @Test
        @DisplayName("returns 0 interest at exactly 45 days")
        void returnsZeroInterest_atExactly45Days() {
            TimeDeposit deposit = new TimeDeposit(1, "premium", 50000.00, 45);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isEqualTo(0.0);
        }

        @Test
        @DisplayName("returns 0 at 44 days even with large balance")
        void returnsZeroInterest_at44Days_withLargeBalance() {
            TimeDeposit deposit = new TimeDeposit(1, "premium", 1000000.00, 44);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("Interest Calculation (>45 days)")
    class InterestCalculation {

        @Test
        @DisplayName("earns interest at 46 days (just after 45-day minimum)")
        void earnsInterest_at46Days() {
            // 50000 * 0.05 / 12 = 208.333...
            TimeDeposit deposit = new TimeDeposit(1, "premium", 50000.00, 46);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isCloseTo(208.33, within(0.01));
        }

        @Test
        @DisplayName("calculates correct monthly interest for standard balance")
        void calculatesCorrectInterest_forStandardBalance() {
            // 50000 * 0.05 / 12 = 208.333...
            TimeDeposit deposit = new TimeDeposit(1, "premium", 50000.00, 60);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isCloseTo(208.33, within(0.01));
        }

        @Test
        @DisplayName("calculates correct interest for large balance")
        void calculatesCorrectInterest_forLargeBalance() {
            // 1000000 * 0.05 / 12 = 4166.666...
            TimeDeposit deposit = new TimeDeposit(1, "premium", 1000000.00, 100);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isCloseTo(4166.67, within(0.01));
        }

        @Test
        @DisplayName("calculates correct interest for small balance")
        void calculatesCorrectInterest_forSmallBalance() {
            // 1000 * 0.05 / 12 = 4.166...
            TimeDeposit deposit = new TimeDeposit(1, "premium", 1000.00, 60);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isCloseTo(4.17, within(0.01));
        }

        @Test
        @DisplayName("returns 0 interest for zero balance")
        void returnsZeroInterest_forZeroBalance() {
            TimeDeposit deposit = new TimeDeposit(1, "premium", 0.0, 60);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isEqualTo(0.0);
        }

        @Test
        @DisplayName("continues to earn interest at very long durations")
        void earnsInterest_atLongDuration() {
            // 50000 * 0.05 / 12 = 208.333...
            TimeDeposit deposit = new TimeDeposit(1, "premium", 50000.00, 1000);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isCloseTo(208.33, within(0.01));
        }
    }

    @Nested
    @DisplayName("Edge Cases at 45/46 Day Boundary")
    class BoundaryEdgeCases {

        @Test
        @DisplayName("45 days earns no interest, 46 days earns interest")
        void verifyBoundaryBehavior() {
            TimeDeposit at45Days = new TimeDeposit(1, "premium", 50000.00, 45);
            TimeDeposit at46Days = new TimeDeposit(2, "premium", 50000.00, 46);

            assertThat(strategy.calculateInterest(at45Days)).isEqualTo(0.0);
            assertThat(strategy.calculateInterest(at46Days)).isGreaterThan(0.0);
        }

        @Test
        @DisplayName("large balance at boundary shows dramatic difference")
        void largeBoundaryDifference() {
            TimeDeposit at45Days = new TimeDeposit(1, "premium", 10000000.00, 45);
            TimeDeposit at46Days = new TimeDeposit(2, "premium", 10000000.00, 46);

            double interestAt45 = strategy.calculateInterest(at45Days);
            double interestAt46 = strategy.calculateInterest(at46Days);

            assertThat(interestAt45).isEqualTo(0.0);
            // 10000000 * 0.05 / 12 = 41666.666...
            assertThat(interestAt46).isCloseTo(41666.67, within(0.01));
        }
    }
}

