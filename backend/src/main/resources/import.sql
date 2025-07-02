-- Insert some default values into the database.

INSERT INTO accounts (
    label,
    start_balance,
    active,
    deposits,
    created_at,
    modified_at
)
VALUES (
    'Example Account',
    1000,
    TRUE,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
INSERT INTO categories (
    label,
    transaction_type,
    smooth_type,
    created_at,
    modified_at
)
VALUES ('Salary', 'INCOME', 'MONTHLY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO categories (
    label,
    transaction_type,
    smooth_type,
    created_at,
    modified_at
)
VALUES ('Food', 'EXPENSE', 'MONTHLY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
