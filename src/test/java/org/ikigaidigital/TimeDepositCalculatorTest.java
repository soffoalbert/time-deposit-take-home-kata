package org.ikigaidigital;

import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.TimeDepositCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

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
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, "basic", 10000.00, 30));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isEqualTo(10000.00);
        }

        @Test
        @DisplayName("Student plan - no interest at 30 days")
        void studentPlan_noInterest_at30Days() {
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, "student", 5000.00, 30));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isEqualTo(5000.00);
        }

        @Test
        @DisplayName("Premium plan - no interest at 30 days")
        void premiumPlan_noInterest_at30Days() {
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, "premium", 50000.00, 30));
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
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, "basic", 10000.00, 45));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isCloseTo(10008.33, within(0.01));
        }

        @Test
        @DisplayName("Original test - basic plan large balance")
        void basicPlan_largeBalance() {
            // 1234567 * 0.01 / 12 = 1028.81
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, "basic", 1234567.00, 45));
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
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, "student", 5000.00, 100));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isCloseTo(5012.50, within(0.01));
        }

        @Test
        @DisplayName("Student plan earns interest at 365 days")
        void studentPlan_earnsInterest_at365Days() {
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, "student", 5000.00, 365));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isCloseTo(5012.50, within(0.01));
        }

        @Test
        @DisplayName("Student plan no interest at 366+ days")
        void studentPlan_noInterest_at366Days() {
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, "student", 5000.00, 366));
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
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, "premium", 50000.00, 45));
            calculator.updateBalance(plans);
            assertThat(plans.get(0).getBalance()).isEqualTo(50000.00);
        }

        @Test
        @DisplayName("Premium plan earns 5% annual interest after 45 days")
        void premiumPlan_earnsInterest_after45Days() {
            // 50000 * 0.05 / 12 = 208.33
            List<TimeDeposit> plans = List.of(new TimeDeposit(1, "premium", 50000.00, 60));
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
                    new TimeDeposit(1, "basic", 10000.00, 45),
                    new TimeDeposit(2, "student", 5000.00, 100),
                    new TimeDeposit(3, "premium", 50000.00, 60)
            ));
            calculator.updateBalance(plans);

            assertThat(plans.get(0).getBalance()).isCloseTo(10008.33, within(0.01)); // basic
            assertThat(plans.get(1).getBalance()).isCloseTo(5012.50, within(0.01));  // student
            assertThat(plans.get(2).getBalance()).isCloseTo(50208.33, within(0.01)); // premium
        }
    }
}
