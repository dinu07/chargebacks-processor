-- Insert mock data based on today's date
-- This script uses MySQL functions to generate dates relative to the current date

INSERT INTO Chargebacks (disputed_dt, disputed_amt, disputed_curr, merchandise_ref, reason_for_dispute, created_time) VALUES
-- Records from 5 days ago
(DATE_SUB(CURDATE(), INTERVAL 5 DAY), 150.75, 'USD', 'REF-001', 'Product not received', DATE_SUB(NOW(), INTERVAL 5 DAY) + INTERVAL 8 HOUR),
(DATE_SUB(CURDATE(), INTERVAL 5 DAY), 89.50, 'EUR', 'REF-002', 'Item damaged', DATE_SUB(NOW(), INTERVAL 5 DAY) + INTERVAL 14 HOUR),

-- Records from 4 days ago
(DATE_SUB(CURDATE(), INTERVAL 4 DAY), 250.00, 'GBP', 'REF-003', 'Unauthorized transaction', DATE_SUB(NOW(), INTERVAL 4 DAY) + INTERVAL 9 HOUR),
(DATE_SUB(CURDATE(), INTERVAL 4 DAY), 199.99, 'USD', 'REF-004', 'Duplicate charge', DATE_SUB(NOW(), INTERVAL 4 DAY) + INTERVAL 16 HOUR),

-- Records from 3 days ago
(DATE_SUB(CURDATE(), INTERVAL 3 DAY), 75.25, 'EUR', 'REF-005', 'Fraudulent transaction', DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 10 HOUR),
(DATE_SUB(CURDATE(), INTERVAL 3 DAY), 320.50, 'USD', 'REF-006', 'Service not provided', DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 15 HOUR),

-- Records from 2 days ago
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 125.00, 'GBP', 'REF-007', 'Wrong item received', DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 11 HOUR),
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 450.75, 'USD', 'REF-008', 'Subscription not cancelled', DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 17 HOUR),

-- Records from yesterday
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 99.99, 'EUR', 'REF-009', 'Quality not as described', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 8 HOUR),
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 275.50, 'USD', 'REF-010', 'Charged twice', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 13 HOUR),

-- Records from today
(CURDATE(), 180.00, 'GBP', 'REF-011', 'Product defective', NOW() - INTERVAL 6 HOUR),
(CURDATE(), 95.25, 'EUR', 'REF-012', 'Order never arrived', NOW() - INTERVAL 2 HOUR),

-- Future records (for testing date range filtering)
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), 500.00, 'USD', 'REF-013', 'Future transaction', DATE_ADD(NOW(), INTERVAL 1 DAY)),
(DATE_ADD(CURDATE(), INTERVAL 2 DAY), 350.75, 'EUR', 'REF-014', 'Future transaction 2', DATE_ADD(NOW(), INTERVAL 2 DAY));

