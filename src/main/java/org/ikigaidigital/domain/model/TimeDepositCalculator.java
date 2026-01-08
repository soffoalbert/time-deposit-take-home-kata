package org.ikigaidigital.domain.model;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Domain service for calculating time deposit interest.
 * Contains the core business logic for interest calculation.
 * 
 * Business Rules:
 * - No interest for the first 30 days (grace period)
 * - Student plan: 3% annual rate, only for deposits less than 366 days
 * - Premium plan: 5% annual rate, only after 45 days
 * - Basic plan: 1% annual rate
 * 
 * This is a pure domain class with no framework dependencies.
 * Spring configuration is done via @Bean in infrastructure layer.
 */
public class TimeDepositCalculator {
    
    /**
     * Updates the balance of all time deposits by applying monthly interest.
     * 
     * @param xs the list of time deposits to update
     */
    public void updateBalance(List<TimeDeposit> xs) {
        for (int i = 0; i < xs.size(); i++) {
            double interest = 0;

            if (xs.get(i).getDays() > 30) {
                if (xs.get(i).getPlanType().equals("student")) {
                    if (xs.get(i).getDays() < 366) {
                        interest += xs.get(i).getBalance() * 0.03 / 12;
                    }
                } else if (xs.get(i).getPlanType().equals("premium")) {
                    if (xs.get(i).getDays() > 45) {
                        interest += xs.get(i).getBalance() * 0.05 / 12;
                    }
                } else if (xs.get(i).getPlanType().equals("basic")) {
                    interest += xs.get(i).getBalance() * 0.01 / 12;
                }
            }

            double a2d = xs.get(i).getBalance() + (new BigDecimal(interest).setScale(2, RoundingMode.HALF_UP)).doubleValue();
            xs.get(i).setBalance(a2d);
        }
    }
}

