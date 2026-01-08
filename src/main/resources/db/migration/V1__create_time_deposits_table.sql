-- V1__create_time_deposits_table.sql
-- Creates the time_deposits table for storing time deposit accounts

CREATE TABLE time_deposits (
    id SERIAL PRIMARY KEY,
    plan_type VARCHAR(50) NOT NULL CHECK (plan_type IN ('basic', 'student', 'premium')),
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    days INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for querying by plan type
CREATE INDEX idx_time_deposits_plan_type ON time_deposits(plan_type);

