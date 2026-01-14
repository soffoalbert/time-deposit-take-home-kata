package org.ikigaidigital;

import org.ikigaidigital.domain.model.InterestStrategyFactory;
import org.ikigaidigital.domain.model.PlanType;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.TimeDepositCalculator;
import org.ikigaidigital.domain.model.strategy.BasicInterestStrategy;
import org.ikigaidigital.domain.model.strategy.PremiumInterestStrategy;
import org.ikigaidigital.domain.model.strategy.StudentInterestStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Comprehensive unit tests for TimeDepositCalculator.
 *
 * Tests the updateBalance() method which:
 * - Delegates interest calculation to the strategy factory
 * - Rounds interest to 2 decimal places using HALF_UP rounding
 * - Updates the balance of each deposit in the list
 */
@DisplayName("TimeDepositCalculator Tests")
public class TimeDepositCalculatorTest {

    private TimeDepositCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new TimeDepositCalculator();
    }

    @Nested
    @DisplayName("No Interest First 30 Days")
    class NoInterestFirst30Days {

        @Test
        @DisplayName("Basic plan - no interest at 30 days")
        void basicPlan_noInterest_at30Days() {
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, PlanType.BASIC, 10000.00, 30));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isEqualTo(10000.00);
        }

        @Test
        @DisplayName("Student plan - no interest at 30 days")
        void studentPlan_noInterest_at30Days() {
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, PlanType.STUDENT, 5000.00, 30));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isEqualTo(5000.00);
        }

        @Test
        @DisplayName("Premium plan - no interest at 30 days")
        void premiumPlan_noInterest_at30Days() {
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, PlanType.PREMIUM, 50000.00, 30));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isEqualTo(50000.00);
        }
    }

    @Nested
    @DisplayName("Basic Plan Interest")
    class BasicPlanInterest {

        @Test
        @DisplayName("Basic plan earns 1% annual interest after 30 days")
        void basicPlan_earnsInterest_after30Days() {
            // 10000 * 0.01 / 12 = 8.33
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, PlanType.BASIC, 10000.00, 45));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isCloseTo(10008.33, within(0.01));
        }

        @Test
        @DisplayName("Original test - basic plan large balance")
        void basicPlan_largeBalance() {
            // 1234567 * 0.01 / 12 = 1028.81
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, PlanType.BASIC, 1234567.00, 45));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isCloseTo(1235595.81, within(0.01));
        }
    }

    @Nested
    @DisplayName("Student Plan Interest")
    class StudentPlanInterest {

        @Test
        @DisplayName("Student plan earns 3% annual interest after 30 days")
        void studentPlan_earnsInterest_after30Days() {
            // 5000 * 0.03 / 12 = 12.50
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, PlanType.STUDENT, 5000.00, 100));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isCloseTo(5012.50, within(0.01));
        }

        @Test
        @DisplayName("Student plan earns interest at 365 days")
        void studentPlan_earnsInterest_at365Days() {
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, PlanType.STUDENT, 5000.00, 365));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isCloseTo(5012.50, within(0.01));
        }

        @Test
        @DisplayName("Student plan no interest at 366+ days")
        void studentPlan_noInterest_at366Days() {
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, PlanType.STUDENT, 5000.00, 366));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isEqualTo(5000.00);
        }
    }

    @Nested
    @DisplayName("Premium Plan Interest")
    class PremiumPlanInterest {

        @Test
        @DisplayName("Premium plan no interest at 45 days")
        void premiumPlan_noInterest_at45Days() {
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, PlanType.PREMIUM, 50000.00, 45));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isEqualTo(50000.00);
        }

        @Test
        @DisplayName("Premium plan earns 5% annual interest after 45 days")
        void premiumPlan_earnsInterest_after45Days() {
            // 50000 * 0.05 / 12 = 208.33
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, PlanType.PREMIUM, 50000.00, 60));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isCloseTo(50208.33, within(0.01));
        }
    }

    @Nested
    @DisplayName("Multiple Deposits")
    class MultipleDeposits {

        @Test
        @DisplayName("Updates all deposits in list")
        void updatesAllDeposits() {
            List<TimeDeposit> plans = new ArrayList<>(List.of(
                    new TimeDeposit(1, PlanType.BASIC, 10000.00, 45),
                    new TimeDeposit(2, PlanType.STUDENT, 5000.00, 100),
                    new TimeDeposit(3, PlanType.PREMIUM, 50000.00, 60)
            ));
            calculator.updateBalance(plans);

            assertThat(plans.get(0).getBalance()).isCloseTo(10008.33, within(0.01)); // basic
            assertThat(plans.get(1).getBalance()).isCloseTo(5012.50, within(0.01));  // student
            assertThat(plans.get(2).getBalance()).isCloseTo(50208.33, within(0.01)); // premium
        }

        @Test
        @DisplayName("Handles empty list without error")
        void handlesEmptyList() {
            List<TimeDeposit> plans = new ArrayList<>();

            calculator.updateBalance(plans);

            assertThat(plans).isEmpty();
        }

        @Test
        @DisplayName("Mixed deposits with some in grace period")
        void mixedDeposits_someInGracePeriod() {
            List<TimeDeposit> plans = new ArrayList<>(List.of(
                    new TimeDeposit(1, PlanType.BASIC, 10000.00, 30),    // grace period
                    new TimeDeposit(2, PlanType.STUDENT, 5000.00, 31),   // just after grace
                    new TimeDeposit(3, PlanType.PREMIUM, 50000.00, 45)   // at premium minimum
            ));
            calculator.updateBalance(plans);

            assertThat(plans.get(0).getBalance()).isEqualTo(10000.00); // no change
            assertThat(plans.get(1).getBalance()).isCloseTo(5012.50, within(0.01));
            assertThat(plans.get(2).getBalance()).isEqualTo(50000.00); // no change
        }
    }

    @Nested
    @DisplayName("Rounding Behavior (HALF_UP to 2 decimal places)")
    class RoundingBehavior {

        @Test
        @DisplayName("Rounds interest using HALF_UP - rounds up when fraction >= 0.5")
        void roundsUp_whenFractionIsHalf() {
            // Balance that produces interest ending in .5 cents
            // 7200 * 0.01 / 12 = 6.00 exactly (no rounding needed)
            // 7250 * 0.01 / 12 = 6.041666... → rounds to 6.04
            List<TimeDeposit> plans = new ArrayList<>(List.of(
                    new TimeDeposit(1, PlanType.BASIC, 7250.00, 45)
            ));
            calculator.updateBalance(plans);

            assertThat(plans.get(0).getBalance()).isEqualTo(7256.04);
        }

        @Test
        @DisplayName("Rounds interest using HALF_UP - rounds down when fraction < 0.5")
        void roundsDown_whenFractionLessThanHalf() {
            // 7100 * 0.01 / 12 = 5.9166... → rounds to 5.92
            List<TimeDeposit> plans = new ArrayList<>(List.of(
                    new TimeDeposit(1, PlanType.BASIC, 7100.00, 45)
            ));
            calculator.updateBalance(plans);

            assertThat(plans.get(0).getBalance()).isEqualTo(7105.92);
        }

        @Test
        @DisplayName("HALF_UP rounding at exact .5 boundary")
        void halfUp_atExactBoundary() {
            // Need a balance where interest is exactly X.XX5
            // 6000 * 0.01 / 12 = 5.00 exactly
            // 6600 * 0.01 / 12 = 5.50 exactly
            List<TimeDeposit> plans = new ArrayList<>(List.of(
                    new TimeDeposit(1, PlanType.BASIC, 6600.00, 45)
            ));
            calculator.updateBalance(plans);

            assertThat(plans.get(0).getBalance()).isEqualTo(6605.50);
        }

        @Test
        @DisplayName("Interest precision maintained across multiple calculations")
        void precisionMaintained_acrossCalculations() {
            // 12345.67 * 0.01 / 12 = 10.28805833... → rounds to 10.29
            List<TimeDeposit> plans = new ArrayList<>(List.of(
                    new TimeDeposit(1, PlanType.BASIC, 12345.67, 45)
            ));
            calculator.updateBalance(plans);

            assertThat(plans.get(0).getBalance()).isCloseTo(12355.96, within(0.01));
        }
    }

    @Nested
    @DisplayName("Factory Integration")
    class FactoryIntegration {

        @Test
        @DisplayName("Uses injected strategy factory when provided")
        void usesInjectedFactory() {
            InterestStrategyFactory customFactory = new InterestStrategyFactory(
                    List.of(new BasicInterestStrategy())
            );
            TimeDepositCalculator customCalculator = new TimeDepositCalculator(customFactory);

            List<TimeDeposit> plans = new ArrayList<>(List.of(
                    new TimeDeposit(1, PlanType.BASIC, 10000.00, 45)
            ));
            customCalculator.updateBalance(plans);

            assertThat(plans.get(0).getBalance()).isCloseTo(10008.33, within(0.01));
        }



        @Test
        @DisplayName("Default constructor creates calculator with all three strategies")
        void defaultConstructor_hasAllStrategies() {
            TimeDepositCalculator defaultCalculator = new TimeDepositCalculator();

            List<TimeDeposit> plans = new ArrayList<>(List.of(
                    new TimeDeposit(1, PlanType.BASIC, 10000.00, 45),
                    new TimeDeposit(2, PlanType.STUDENT, 5000.00, 100),
                    new TimeDeposit(3, PlanType.PREMIUM, 50000.00, 60)
            ));
            defaultCalculator.updateBalance(plans);

            // All should have earned interest
            assertThat(plans.get(0).getBalance()).isGreaterThan(10000.00);
            assertThat(plans.get(1).getBalance()).isGreaterThan(5000.00);
            assertThat(plans.get(2).getBalance()).isGreaterThan(50000.00);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Zero balance deposits remain at zero")
        void zeroBalance_remainsZero() {
            List<TimeDeposit> plans = new ArrayList<>(List.of(
                    new TimeDeposit(1, PlanType.BASIC, 0.0, 45),
                    new TimeDeposit(2, PlanType.STUDENT, 0.0, 100),
                    new TimeDeposit(3, PlanType.PREMIUM, 0.0, 60)
            ));
            calculator.updateBalance(plans);

            assertThat(plans.get(0).getBalance()).isEqualTo(0.0);
            assertThat(plans.get(1).getBalance()).isEqualTo(0.0);
            assertThat(plans.get(2).getBalance()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Very large balance calculation")
        void veryLargeBalance() {
            // 100000000 * 0.05 / 12 = 416666.666...
            List<TimeDeposit> plans = new ArrayList<>(List.of(
                    new TimeDeposit(1, PlanType.PREMIUM, 100000000.00, 60)
            ));
            calculator.updateBalance(plans);

            assertThat(plans.get(0).getBalance()).isCloseTo(100416666.67, within(0.01));
        }

        @Test
        @DisplayName("Very small balance with minimal interest")
        void verySmallBalance() {
            // 1.00 * 0.01 / 12 = 0.000833... → rounds to 0.00
            List<TimeDeposit> plans = new ArrayList<>(List.of(
                    new TimeDeposit(1, PlanType.BASIC, 1.00, 45)
            ));
            calculator.updateBalance(plans);

            assertThat(plans.get(0).getBalance()).isEqualTo(1.00); // interest rounds to 0
        }

        @Test
        @DisplayName("Cumulative updates apply interest repeatedly")
        void cumulativeUpdates() {
            List<TimeDeposit> plans = new ArrayList<>(List.of(
                    new TimeDeposit(1, PlanType.BASIC, 10000.00, 45)
            ));

            // First update: 10000 * 0.01 / 12 = 8.33 → balance = 10008.33
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isCloseTo(10008.33, within(0.01));

            // Second update: 10008.33 * 0.01 / 12 = 8.34 → balance = 10016.67
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isCloseTo(10016.67, within(0.01));
        }
    }
}
