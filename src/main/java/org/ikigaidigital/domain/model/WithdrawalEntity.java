package org.ikigaidigital.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a withdrawal from a time deposit.
 * Maps to the withdrawals table in the database.
 */
@Entity
@Table(name = "withdrawals")
public class WithdrawalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_deposit_id", nullable = false)
    private TimeDepositEntity timeDeposit;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "withdrawal_date", nullable = false)
    private LocalDate withdrawalDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Default constructor required by JPA
    public WithdrawalEntity() {
    }

    public WithdrawalEntity(TimeDepositEntity timeDeposit, BigDecimal amount, LocalDate withdrawalDate) {
        this.timeDeposit = timeDeposit;
        this.amount = amount;
        this.withdrawalDate = withdrawalDate;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TimeDepositEntity getTimeDeposit() {
        return timeDeposit;
    }

    public void setTimeDeposit(TimeDepositEntity timeDeposit) {
        this.timeDeposit = timeDeposit;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getWithdrawalDate() {
        return withdrawalDate;
    }

    public void setWithdrawalDate(LocalDate withdrawalDate) {
        this.withdrawalDate = withdrawalDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

