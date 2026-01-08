-- V3__seed_sample_data.sql
-- Insert sample time deposits (one of each plan type) for testing

INSERT INTO time_deposits (plan_type, balance, days, created_at, updated_at)
VALUES
    ('basic', 10000.00, 45, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('student', 5000.00, 60, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('premium', 50000.00, 90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample withdrawals linked to deposits
INSERT INTO withdrawals (time_deposit_id, amount, withdrawal_date, created_at)
VALUES
    (1, 500.00, '2024-01-15', CURRENT_TIMESTAMP),
    (1, 200.00, '2024-02-01', CURRENT_TIMESTAMP),
    (2, 1000.00, '2024-01-20', CURRENT_TIMESTAMP),
    (3, 5000.00, '2024-01-10', CURRENT_TIMESTAMP);

