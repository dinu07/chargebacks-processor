-- Create the Chargebacks table
CREATE TABLE IF NOT EXISTS Chargebacks (
    disputed_dt DATE,
    disputed_amt DECIMAL(19, 2),
    disputed_curr VARCHAR(3),
    merchandise_ref VARCHAR(255),
    reason_for_dispute VARCHAR(500),
    created_time TIMESTAMP,
    INDEX idx_created_time (created_time)
);

