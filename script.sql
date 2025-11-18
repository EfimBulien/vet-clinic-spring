----------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS vet_visits, cat_owners, cats, owners, vets CASCADE;

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_userroles_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_userroles_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
);

CREATE TABLE cats (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    age INT DEFAULT 1 CHECK (age >= 1),
    breed VARCHAR(256)
);

CREATE TABLE owners (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(256) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    email VARCHAR(256) UNIQUE,
    address TEXT,
    user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE cat_owners (
    cat_id BIGINT NOT NULL REFERENCES cats(id) ON DELETE CASCADE,
    owner_id BIGINT NOT NULL REFERENCES owners(id) ON DELETE CASCADE,
    ownership_start_date DATE DEFAULT CURRENT_DATE,
    ownership_end_date DATE,
    PRIMARY KEY (cat_id, owner_id),
    CHECK (ownership_end_date IS NULL OR ownership_end_date > ownership_start_date)
);

CREATE TABLE vets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    specialty VARCHAR(256),
    clinic_name VARCHAR(256) NOT NULL,
    phone VARCHAR(20) UNIQUE
);

CREATE TABLE vet_visits (
    id BIGSERIAL PRIMARY KEY,
    cat_id BIGINT NOT NULL REFERENCES cats(id) ON DELETE CASCADE,
    vet_id BIGINT NOT NULL REFERENCES vets(id) ON DELETE RESTRICT,
    visit_date DATE NOT NULL DEFAULT CURRENT_DATE,
    diagnosis TEXT,
    treatment TEXT,
    cost DECIMAL(10, 2) CHECK (cost >= 0),
    CHECK (visit_date <= CURRENT_DATE)
);

INSERT INTO roles (name) VALUES
('USER'),
('MANAGER'),
('ADMIN');

INSERT INTO cats (name, age, breed) VALUES
('Мурка', 3, 'Сибирская'),
('Барсик', 5, 'Британская'),
('Снежок', 2, 'Мейн-кун'),
('Луна', 7, 'Сиамская'),
('Рыжик', 4, 'Персидская'),
('Тиша', 1, 'Бенгальская'),
('Матроскин', 6, 'Дворовая'),
('Клеопатра', 9, 'Сфинкс'),
('Васька', 3, 'Шотландская вислоухая'),
('Пушок', 8, 'Русская голубая');

INSERT INTO owners (full_name, phone, email, address) VALUES
('Иванов Иван Иванович', '+79991234567', 'ivanov@example.com', 'Москва, ул. Ленина, 1'),
('Петрова Анна Сергеевна', '+79997654321', 'petrova@example.com', 'СПб, пр. Невский, 25'),
('Сидоров Петр Михайлович', '+7916123321', 'sidorov@example.com', 'Казань, ул. Баумана, 10');

INSERT INTO vets (name, specialty, clinic_name, phone) VALUES
('Доктор Айболит', 'Терапия', 'ВетКлиника Здоровье', '+78001234567'),
('Доктор Дулитл', 'Хирургия', 'ВетЦентр Плюс', '+78009876543');

INSERT INTO cat_owners (cat_id, owner_id, ownership_start_date) VALUES
(1, 1, '2023-01-15'),
(2, 2, '2022-06-20'),
(3, 1, '2024-03-10'),
(4, 3, '2021-11-01'),
(5, 2, '2023-09-05');

INSERT INTO vet_visits (cat_id, vet_id, visit_date, diagnosis, treatment, cost) VALUES
(1, 1, '2025-02-10', 'Простуда', 'Антибиотики', 2500.00),
(2, 2, '2025-01-05', 'Перелом лапы', 'Операция + гипс', 15000.50),
(3, 1, '2025-03-20', 'Вакцинация', 'Прививка от бешенства', 1200.00);

----------------------------------------------------------------------------------------------------------------
