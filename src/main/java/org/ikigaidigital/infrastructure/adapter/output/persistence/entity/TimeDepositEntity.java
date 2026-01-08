package org.ikigaidigital.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity representing a time deposit account.
 * Maps to the time_deposits table in the database.
 */
@Entity
@Table(name = "time_deposits")
public class TimeDepositEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "plan_type", nullable = false, length = 50)
    private String planType;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "days", nullable = false)
    private Integer days;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "timeDeposit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<WithdrawalEntity> withdrawals = new ArrayList<>();

    // Default constructor required by JPA
    public TimeDepositEntity() {
    }

    public TimeDepositEntity(String planType, BigDecimal balance, Integer days) {
        this.planType = planType;
        this.balance = balance;
        this.days = days;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<WithdrawalEntity> getWithdrawals() {
        return withdrawals;
    }

    public void setWithdrawals(List<WithdrawalEntity> withdrawals) {
        this.withdrawals = withdrawals;
    }

    public void addWithdrawal(WithdrawalEntity withdrawal) {
        withdrawals.add(withdrawal);
        withdrawal.setTimeDeposit(this);
    }

    public void removeWithdrawal(WithdrawalEntity withdrawal) {
        withdrawals.remove(withdrawal);
        withdrawal.setTimeDeposit(null);
    }
}

