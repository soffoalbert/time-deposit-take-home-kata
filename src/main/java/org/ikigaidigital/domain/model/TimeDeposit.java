package org.ikigaidigital.domain.model;

/**
 * Domain model representing a time deposit account.
 * This is a pure POJO with no framework dependencies.
 */
public class TimeDeposit {
    private int id;
    private PlanType planType;
    private Double balance;
    private int days;

    public TimeDeposit(int id, PlanType planType, Double balance, int days) {
        this.id = id;
        this.planType = planType;
        this.balance = balance;
        this.days = days;
    }

    public int getId() { return id; }

    public PlanType getPlanType() {
        return planType;
    }

    public Double getBalance() {
        return balance;
    }

    public int getDays() {
        return days;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
