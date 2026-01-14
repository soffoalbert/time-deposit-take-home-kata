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
 * Unit tests for InternalInterestStrategy.
 * 
 * Business Rules:
 * - 8.5% annual interest rate (0.085 / 12 = ~0.007083 monthly)
 * - Interest accrues from day 1 (no grace period)
 * - Plan terminates at exactly 300 days
 * - Balance is reset to 0 when plan terminates (day 300+)
 */
@DisplayName("InternalInterestStrategy Tests")
class InternalInterestStrategyTest {

    private InternalInterestStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new InternalInterestStrategy();
    }

    @Nested
    @DisplayName("supports() method")
    class SupportsMethod {

        @Test
        @DisplayName("returns true for 'internal' plan type")
        void returnsTrue_forInternalPlanType() {
            assertThat(strategy.supports("internal")).isTrue();
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
        @DisplayName("returns false for 'premium' plan type")
        void returnsFalse_forPremiumPlanType() {
            assertThat(strategy.supports("premium")).isFalse();
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
        @DisplayName("returns false for 'INTERNAL' (case sensitive)")
        void returnsFalse_forUppercaseInternal() {
            assertThat(strategy.supports("INTERNAL")).isFalse();
        }
    }

    @Nested
    @DisplayName("Interest Calculation (Day 1 to Day 299)")
    class InterestCalculation {

        @Test
        @DisplayName("earns interest from day 1 (no grace period)")
        void earnsInterest_fromDay1() {
            // 10000 * 0.085 / 12 = 70.833...
            TimeDeposit deposit = new TimeDeposit(1, "internal", 10000.00, 1);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isCloseTo(70.83, within(0.01));
        }

        @ParameterizedTest(name = "earns interest at {0} days")
        @ValueSource(ints = {1, 5, 10, 30, 45, 100, 150, 200, 250})
        @DisplayName("earns interest at various days before termination")
        void earnsInterest_beforeTermination(int days) {
            TimeDeposit deposit = new TimeDeposit(1, "internal", 10000.00, days);
            
            double interest = strategy.calculateInterest(deposit);
            
            // 10000 * 0.085 / 12 = 70.833...
            assertThat(interest).isCloseTo(70.83, within(0.01));
        }

        @Test
        @DisplayName("calculates correct monthly interest for standard balance")
        void calculatesCorrectInterest_forStandardBalance() {
            // 10000 * 0.085 / 12 = 70.833...
            TimeDeposit deposit = new TimeDeposit(1, "internal", 10000.00, 100);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isCloseTo(70.83, within(0.01));
        }

        @Test
        @DisplayName("calculates correct interest for large balance")
        void calculatesCorrectInterest_forLargeBalance() {
            // 1000000 * 0.085 / 12 = 7083.333...
            TimeDeposit deposit = new TimeDeposit(1, "internal", 10000000000.00, 150);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isCloseTo(7083.33, within(0.01));
        }

        @Test
        @DisplayName("calculates correct interest for small balance")
        void calculatesCorrectInterest_forSmallBalance() {
            // 100 * 0.085 / 12 = 0.708333...
            TimeDeposit deposit = new TimeDeposit(1, "internal", 100.00, 50);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isCloseTo(0.71, within(0.01));
        }

        @Test
        @DisplayName("returns 0 interest for zero balance")
        void returnsZeroInterest_forZeroBalance() {
            TimeDeposit deposit = new TimeDeposit(1, "internal", 0.0, 100);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isEqualTo(0.0);
        }

        @Test
        @DisplayName("earns interest at day 299 (last day before termination)")
        void earnsInterest_atDay299() {
            // 10000 * 0.085 / 12 = 70.833...
            TimeDeposit deposit = new TimeDeposit(1, "internal", 10000.00, 299);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isCloseTo(70.83, within(0.01));
        }
    }

    @Nested
    @DisplayName("Plan Termination (Day 300+)")
    class PlanTermination {

        @Test
        @DisplayName("returns 0 interest at exactly 300 days (termination day)")
        void returnsZeroInterest_at300Days() {
            TimeDeposit deposit = new TimeDeposit(1, "internal", 10000.00, 300);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isEqualTo(0.0);
        }

        @Test
        @DisplayName("returns 0 interest at 301 days (after termination)")
        void returnsZeroInterest_at301Days() {
            TimeDeposit deposit = new TimeDeposit(1, "internal", 10000.00, 301);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isEqualTo(0.0);
        }

        @ParameterizedTest(name = "returns 0 interest at {0} days")
        @ValueSource(ints = {300, 301, 350, 400, 500, 1000})
        @DisplayName("returns 0 interest at and after termination day")
        void returnsZeroInterest_afterTermination(int days) {
            TimeDeposit deposit = new TimeDeposit(1, "internal", 10000.00, days);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isEqualTo(0.0);
        }

        @Test
        @DisplayName("returns 0 interest at termination even with large balance")
        void returnsZeroInterest_atTermination_withLargeBalance() {
            TimeDeposit deposit = new TimeDeposit(1, "internal", 1000000.00, 300);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("Edge Cases at 299/300 Day Boundary")
    class BoundaryEdgeCases {

        @Test
        @DisplayName("299 days earns interest, 300 days earns no interest")
        void verifyBoundaryBehavior() {
            TimeDeposit at299Days = new TimeDeposit(1, "internal", 10000.00, 299);
            TimeDeposit at300Days = new TimeDeposit(2, "internal", 10000.00, 300);

            assertThat(strategy.calculateInterest(at299Days)).isGreaterThan(0.0);
            assertThat(strategy.calculateInterest(at300Days)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("verifies exact interest amount at day 299 vs day 300")
        void verifyExactInterestAtBoundary() {
            TimeDeposit at299Days = new TimeDeposit(1, "internal", 10000.00, 299);
            TimeDeposit at300Days = new TimeDeposit(2, "internal", 10000.00, 300);

            // 10000 * 0.085 / 12 = 70.833...
            assertThat(strategy.calculateInterest(at299Days)).isCloseTo(70.83, within(0.01));
            assertThat(strategy.calculateInterest(at300Days)).isEqualTo(0.0);
        }
    }
}
