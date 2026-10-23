CREATE DATABASE IF NOT EXISTS viptables;
USE viptables;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'MANAGER') NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS restaurants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    address VARCHAR(255) NOT NULL,
    cuisine VARCHAR(80) NOT NULL,
    image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS restaurant_tables (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    restaurant_id BIGINT NOT NULL,
    table_number VARCHAR(20) NOT NULL,
    capacity INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_table_restaurant
        FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_restaurant_table_number
        UNIQUE (restaurant_id, table_number),
    CONSTRAINT chk_table_capacity CHECK (capacity > 0)
);

CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    restaurant_table_id BIGINT NOT NULL,
    reservation_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status ENUM('CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reservation_customer
        FOREIGN KEY (customer_id) REFERENCES users(id),
    CONSTRAINT fk_reservation_table
        FOREIGN KEY (restaurant_table_id) REFERENCES restaurant_tables(id),
    CONSTRAINT chk_reservation_time CHECK (end_time > start_time),
    INDEX idx_reservation_lookup (
        restaurant_table_id,
        reservation_date,
        start_time,
        end_time,
        status
    ),
    INDEX idx_reservation_customer (customer_id, reservation_date, status)
);

-- Development-only accounts. The BCrypt hash below is for the local
-- development password "password"; plaintext passwords are never stored.
INSERT INTO users (full_name, email, password_hash, role)
VALUES
    ('Demo Manager', 'manager@viptables.local', '$2a$10$SSqdLJxq7LAxLe8KiwfuuOImjdCTfIdS.0SIkhqFfLlFJO5ZDLVGe', 'MANAGER'),
    ('Demo Customer', 'customer@viptables.local', '$2a$10$SSqdLJxq7LAxLe8KiwfuuOImjdCTfIdS.0SIkhqFfLlFJO5ZDLVGe', 'CUSTOMER')
ON DUPLICATE KEY UPDATE email = email;

INSERT INTO restaurants (name, description, address, cuisine, image_url)
VALUES
    ('The Copper Table', 'Seasonal plates and warm hospitality.', '12 Market Street', 'Modern European', NULL),
    ('Monsoon Terrace', 'A relaxed dining room for regional flavours.', '8 Garden Avenue', 'Contemporary Indian', NULL)
ON DUPLICATE KEY UPDATE name = name;
