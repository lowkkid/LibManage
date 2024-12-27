-- Вставка ролей
INSERT INTO roles (role_name)
VALUES
    ('Администратор'),
    ('Сотрудник'),
    ('Гость');

-- Вставка пользователей
INSERT INTO users (username, password, role_id)
VALUES
    ('admin', 'admin_pass', 1), -- Администратор
    ('employee1', 'emp1_pass', 2), -- Сотрудник
    ('employee2', 'emp2_pass', 2), -- Сотрудник
    ('guest', 'guest_pass', 3); -- Гость

-- Вставка отделов
INSERT INTO departments (name, manager_id)
VALUES
    ('Отдел разработки', 1),
    ('Отдел продаж', 2);

-- Вставка сотрудников
INSERT INTO employees (user_id, department_id, salary, hire_date, active)
VALUES
    (2, 1, 75000.00, '2023-01-15', TRUE), -- Сотрудник из отдела разработки
    (3, 2, 50000.00, '2023-03-10', TRUE); -- Сотрудник из отдела продаж

-- Вставка зарплат
INSERT INTO salaries (employee_id, payment_date, amount, bonus)
VALUES
    (1, '2023-12-01', 75000.00, 5000.00), -- Зарплата сотрудника 1
    (2, '2023-12-01', 50000.00, 3000.00); -- Зарплата сотрудника 2

-- Вставка корректировок
INSERT INTO adjustments (employee_id, adjustment_date, reason, amount)
VALUES
    (1, '2023-11-20', 'Годовая премия', 10000.00), -- Корректировка для сотрудника 1
    (2, '2023-11-15', 'Премия за успешную сделку', 5000.00); -- Корректировка для сотрудника 2
