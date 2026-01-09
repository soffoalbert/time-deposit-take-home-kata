-- V3__alter_withdrawals_fk.sql
-- Alters the foreign key constraint on withdrawals table to use RESTRICT instead of CASCADE
-- This protects financial records from accidental deletion

-- Drop the existing CASCADE constraint
ALTER TABLE withdrawals DROP CONSTRAINT fk_time_deposit;

-- Re-create with RESTRICT to protect withdrawal history
ALTER TABLE withdrawals
    ADD CONSTRAINT fk_time_deposit
        FOREIGN KEY (time_deposit_id)
        REFERENCES time_deposits(id)
        ON DELETE RESTRICT;

