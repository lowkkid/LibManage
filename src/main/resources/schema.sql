CREATE TABLE IF NOT EXISTS roles (
                                     id SERIAL PRIMARY KEY,
                                     role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
                                     password VARCHAR(100) NOT NULL,
                                     role_id INT NOT NULL,
                                     CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (id)
                                         ON UPDATE CASCADE
                                         ON DELETE RESTRICT
);

CREATE TABLE departments (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(100) NOT NULL UNIQUE,
                             manager_id INT REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE employees (
                           id SERIAL PRIMARY KEY,
                           user_id INT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                           department_id INT REFERENCES Departments(id) ON DELETE SET NULL,
                           salary DECIMAL(10, 2) NOT NULL,
                           hire_date DATE NOT NULL DEFAULT CURRENT_DATE,
                           active BOOLEAN DEFAULT TRUE
);

CREATE TABLE salaries (
                          id SERIAL PRIMARY KEY,
                          employee_id INT NOT NULL REFERENCES Employees(id) ON DELETE CASCADE,
                          payment_date DATE NOT NULL DEFAULT CURRENT_DATE,
                          amount DECIMAL(10, 2) NOT NULL,
                          bonus DECIMAL(10, 2) DEFAULT 0
);

CREATE TABLE adjustments (
                             id SERIAL PRIMARY KEY,
                             employee_id INT NOT NULL REFERENCES Employees(id) ON DELETE CASCADE,
                             adjustment_date DATE NOT NULL DEFAULT CURRENT_DATE,
                             reason VARCHAR(255),
                             amount DECIMAL(10, 2) NOT NULL
);

