-- V2__create_withdrawals_table.sql
-- Creates the withdrawals table for tracking withdrawal transactions

CREATE TABLE withdrawals (
    id SERIAL PRIMARY KEY,
    time_deposit_id INTEGER NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    withdrawal_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_time_deposit
        FOREIGN KEY (time_deposit_id)
        REFERENCES time_deposits(id)
        ON DELETE CASCADE
);

-- Index for querying withdrawals by time deposit
CREATE INDEX idx_withdrawals_time_deposit_id ON withdrawals(time_deposit_id);

