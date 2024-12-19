-- Таблица ролей
CREATE TABLE IF NOT EXISTS roles (
                                     id SERIAL PRIMARY KEY,
                                     role_name VARCHAR(50) NOT NULL UNIQUE
);

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
                                     password VARCHAR(100) NOT NULL,
                                     role_id INT NOT NULL,
                                     CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (id)
                                         ON UPDATE CASCADE
                                         ON DELETE RESTRICT
);

-- Таблица авторов
CREATE TABLE IF NOT EXISTS authors (
                                       id SERIAL PRIMARY KEY,
                                       name VARCHAR(100) NOT NULL
);

-- Таблица жанров
CREATE TABLE IF NOT EXISTS genres (
                                      id SERIAL PRIMARY KEY,
                                      name VARCHAR(50) NOT NULL
);

-- Таблица книг
CREATE TABLE IF NOT EXISTS books (
                                     id SERIAL PRIMARY KEY,
                                     title VARCHAR(200) NOT NULL,
                                     author_id INT NOT NULL,
                                     genre_id INT NOT NULL,
                                     isbn VARCHAR(20) NOT NULL UNIQUE,
                                     available_copies INT NOT NULL DEFAULT 0,
                                     CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES authors (id)
                                         ON UPDATE CASCADE
                                         ON DELETE RESTRICT,
                                     CONSTRAINT fk_genre FOREIGN KEY (genre_id) REFERENCES genres (id)
                                         ON UPDATE CASCADE
                                         ON DELETE RESTRICT
);

-- Таблица бронирований
CREATE TABLE IF NOT EXISTS reservations (
                                            id SERIAL PRIMARY KEY,
                                            user_id INT NOT NULL,
                                            book_id INT NOT NULL,
                                            reservation_date DATE NOT NULL,
                                            status VARCHAR(50) NOT NULL CHECK (status IN ('Активное', 'Выполнено', 'Отменено')),
                                            CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
                                                ON UPDATE CASCADE
                                                ON DELETE CASCADE,
                                            CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books (id)
                                                ON UPDATE CASCADE
                                                ON DELETE RESTRICT
);
