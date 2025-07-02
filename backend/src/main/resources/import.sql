-- Insert some default values into the database.

INSERT INTO accounts (label, start_balance, active, deposits)
VALUES ('Example Account', 1000, TRUE, FALSE);
INSERT INTO categories (label, transaction_type, smooth_type)
VALUES ('Salary', 'INCOME', 'MONTHLY');
INSERT INTO categories (label, transaction_type, smooth_type)
VALUES ('Food', 'EXPENSE', 'MONTHLY');
