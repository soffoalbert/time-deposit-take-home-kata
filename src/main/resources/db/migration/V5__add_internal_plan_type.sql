-- V5__add_internal_plan_type.sql
-- Add 'internal' to the allowed plan types

-- Drop the existing CHECK constraint
ALTER TABLE time_deposits DROP CONSTRAINT IF EXISTS time_deposits_plan_type_check;

-- Add the new CHECK constraint with 'internal' included
ALTER TABLE time_deposits ADD CONSTRAINT time_deposits_plan_type_check 
    CHECK (plan_type IN ('basic', 'student', 'premium', 'internal'));

