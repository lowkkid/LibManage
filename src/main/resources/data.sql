-- Вставка данных в таблицу roles
INSERT INTO roles (role_name) VALUES ('Администратор');
INSERT INTO roles (role_name) VALUES ('Библиотекарь');
INSERT INTO roles (role_name) VALUES ('Читатель');

-- Вставка данных в таблицу users
INSERT INTO users (username, password, role_id) VALUES ('admin', '$2a$10$Dow1NJ1zj6bChE8a7y5nCe7rW6P5Zj1q3zUZk8eIyQKjB4C8eVb1a', 1);
INSERT INTO users (username, password, role_id) VALUES ('librarian1', '$2a$10$Dow1NJ1zj6bChE8a7y5nCe7rW6P5Zj1q3zUZk8eIyQKjB4C8eVb1a', 2);
INSERT INTO users (username, password, role_id) VALUES ('reader1', '$2a$10$Dow1NJ1zj6bChE8a7y5nCe7rW6P5Zj1q3zUZk8eIyQKjB4C8eVb1a', 3);
INSERT INTO users (username, password, role_id) VALUES ('reader2', '$2a$10$Dow1NJ1zj6bChE8a7y5nCe7rW6P5Zj1q3zUZk8eIyQKjB4C8eVb1a', 3);

-- Вставка данных в таблицу authors
INSERT INTO authors (name) VALUES ('Федор Достоевский');
INSERT INTO authors (name) VALUES ('Лев Толстой');
INSERT INTO authors (name) VALUES ('Антон Чехов');

-- Вставка данных в таблицу genres
INSERT INTO genres (name) VALUES ('Роман');
INSERT INTO genres (name) VALUES ('Повесть');
INSERT INTO genres (name) VALUES ('Сказка');

-- Вставка данных в таблицу books
INSERT INTO books (title, author_id, genre_id, isbn, available_copies)
VALUES ('Преступление и наказание', 1, 1, '978-5-394-15141-6', 3);
INSERT INTO books (title, author_id, genre_id, isbn, available_copies)
VALUES ('Война и мир', 2, 1, '978-5-17-080142-6', 2);
INSERT INTO books (title, author_id, genre_id, isbn, available_copies)
VALUES ('Вишневый сад', 3, 2, '978-5-389-04927-6', 1);

-- Вставка данных в таблицу reservations
INSERT INTO reservations (user_id, book_id, reservation_date, status)
VALUES (3, 1, '2024-01-10', 'Активное');
INSERT INTO reservations (user_id, book_id, reservation_date, status)
VALUES (4, 2, '2024-02-15', 'Выполнено');
