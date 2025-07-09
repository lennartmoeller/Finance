-- Insert some default values into the database.

INSERT INTO accounts (label, start_balance, active, deposits)
VALUES ('Example Account', 1000, TRUE, FALSE);
INSERT INTO categories (label, transaction_type, smooth_type)
VALUES ('Salary', 'INCOME', 'MONTHLY');
INSERT INTO categories (label, transaction_type, smooth_type)
VALUES ('Food', 'EXPENSE', 'MONTHLY');

INSERT INTO users (username, password, role) VALUES ('admin', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiBq9VS0Cbvcp1xsHsSOwJWEIDWe5Hi', 'ADMIN');
