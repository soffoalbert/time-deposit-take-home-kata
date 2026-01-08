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
 * Unit tests for StudentInterestStrategy.
 * 
 * Business Rules:
 * - 3% annual interest rate (0.03 / 12 = 0.0025 monthly)
 * - Interest accrues only after 30-day grace period
 * - Interest stops at 366+ days (no interest after 1 year)
 */
@DisplayName("StudentInterestStrategy Tests")
class StudentInterestStrategyTest {

    private StudentInterestStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new StudentInterestStrategy();
    }

    @Nested
    @DisplayName("supports() method")
    class SupportsMethod {

        @Test
        @DisplayName("returns true for 'student' plan type")
        void returnsTrue_forStudentPlanType() {
            assertThat(strategy.supports("student")).isTrue();
        }

        @Test
        @DisplayName("returns false for 'basic' plan type")
        void returnsFalse_forBasicPlanType() {
            assertThat(strategy.supports("basic")).isFalse();
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
        @DisplayName("returns false for 'STUDENT' (case sensitive)")
        void returnsFalse_forUppercaseStudent() {
            assertThat(strategy.supports("STUDENT")).isFalse();
        }
    }

    @Nested
    @DisplayName("Grace Period (≤30 days)")
    class GracePeriod {

        @ParameterizedTest(name = "returns 0 interest at {0} days")
        @ValueSource(ints = {0, 1, 10, 15, 29, 30})
        @DisplayName("returns 0 interest during grace period")
        void returnsZeroInterest_duringGracePeriod(int days) {
            TimeDeposit deposit = new TimeDeposit(1, "student", 5000.00, days);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isEqualTo(0.0);
        }

        @Test
        @DisplayName("returns 0 interest at exactly 30 days with large balance")
        void returnsZeroInterest_atExactly30Days_withLargeBalance() {
            TimeDeposit deposit = new TimeDeposit(1, "student", 100000.00, 30);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("Interest Calculation (31-365 days)")
    class InterestCalculation {

        @Test
        @DisplayName("earns interest at 31 days (just after grace period)")
        void earnsInterest_at31Days() {
            // 5000 * 0.03 / 12 = 12.50
            TimeDeposit deposit = new TimeDeposit(1, "student", 5000.00, 31);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isCloseTo(12.50, within(0.01));
        }

        @Test
        @DisplayName("calculates correct monthly interest for standard balance")
        void calculatesCorrectInterest_forStandardBalance() {
            // 5000 * 0.03 / 12 = 12.50
            TimeDeposit deposit = new TimeDeposit(1, "student", 5000.00, 100);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isCloseTo(12.50, within(0.01));
        }

        @Test
        @DisplayName("calculates correct interest for large balance")
        void calculatesCorrectInterest_forLargeBalance() {
            // 100000 * 0.03 / 12 = 250.00
            TimeDeposit deposit = new TimeDeposit(1, "student", 100000.00, 200);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isCloseTo(250.00, within(0.01));
        }

        @Test
        @DisplayName("calculates correct interest for small balance")
        void calculatesCorrectInterest_forSmallBalance() {
            // 100 * 0.03 / 12 = 0.25
            TimeDeposit deposit = new TimeDeposit(1, "student", 100.00, 60);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isCloseTo(0.25, within(0.01));
        }

        @Test
        @DisplayName("returns 0 interest for zero balance")
        void returnsZeroInterest_forZeroBalance() {
            TimeDeposit deposit = new TimeDeposit(1, "student", 0.0, 100);
            
            double interest = strategy.calculateInterest(deposit);
            
            assertThat(interest).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("366-Day Cutoff (No interest after 1 year)")
    class YearCutoff {

        @Test
        @DisplayName("earns interest at 365 days (last day of eligibility)")
        void earnsInterest_at365Days() {
            // 5000 * 0.03 / 12 = 12.50
            TimeDeposit deposit = new TimeDeposit(1, "student", 5000.00, 365);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isCloseTo(12.50, within(0.01));
        }

        @Test
        @DisplayName("returns 0 interest at exactly 366 days (cutoff point)")
        void returnsZeroInterest_at366Days() {
            TimeDeposit deposit = new TimeDeposit(1, "student", 5000.00, 366);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isEqualTo(0.0);
        }

        @Test
        @DisplayName("returns 0 interest at 367 days (after cutoff)")
        void returnsZeroInterest_at367Days() {
            TimeDeposit deposit = new TimeDeposit(1, "student", 5000.00, 367);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isEqualTo(0.0);
        }

        @Test
        @DisplayName("returns 0 interest at very long duration")
        void returnsZeroInterest_atVeryLongDuration() {
            TimeDeposit deposit = new TimeDeposit(1, "student", 5000.00, 1000);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isEqualTo(0.0);
        }

        @Test
        @DisplayName("returns 0 interest at 366 days even with large balance")
        void returnsZeroInterest_at366Days_withLargeBalance() {
            TimeDeposit deposit = new TimeDeposit(1, "student", 1000000.00, 366);

            double interest = strategy.calculateInterest(deposit);

            assertThat(interest).isEqualTo(0.0);
        }
    }
}

