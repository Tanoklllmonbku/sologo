-- init_db.sql

CREATE DATABASE sologo;

\c sologo;

-- ========== ТАБЛИЦЫ (без ENUM типов) ==========

-- Пользователи
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    nickname VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    passhash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'user' NOT NULL,
    phone_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Города
CREATE TABLE cities (
    city_id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    country VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cities_name ON cities(name);

-- Отели (обновлённая версия)
CREATE TABLE hotels (
    hotel_id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    city_id INTEGER NOT NULL REFERENCES cities(city_id) ON DELETE CASCADE,
    address VARCHAR(300) NOT NULL,
    description TEXT,
    price_per_night INTEGER NOT NULL,
    avg_city_price INTEGER NOT NULL,
    rating FLOAT DEFAULT 0,
    capacity INTEGER DEFAULT 10,  -- ← максимальное количество человек
    manager_phones TEXT,  -- ← массив телефонов в JSON (например: '["+71234567890", "+79876543210"]')
    main_image VARCHAR(500),
    room_images TEXT,
    status INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hotels_city ON hotels(city_id);
CREATE INDEX idx_hotels_price ON hotels(price_per_night);
CREATE INDEX idx_hotels_status ON hotels(status);

-- Бронирования
CREATE TABLE bookings (
    booking_id SERIAL PRIMARY KEY,
    tracking_number VARCHAR(50) UNIQUE NOT NULL,
    user_id INTEGER NOT NULL REFERENCES users(user_id),
    hotel_id INTEGER NOT NULL REFERENCES hotels(hotel_id),
    guests_count INTEGER DEFAULT 1,
    check_in TIMESTAMP NOT NULL,
    check_out TIMESTAMP NOT NULL,
    total_price INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cancelled_at TIMESTAMP
);

CREATE INDEX idx_bookings_user ON bookings(user_id);
CREATE INDEX idx_bookings_hotel ON bookings(hotel_id);
CREATE INDEX idx_bookings_tracking ON bookings(tracking_number);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_dates ON bookings(check_in, check_out);

-- Маршруты
CREATE TABLE routes (
    route_id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    mood VARCHAR(50) NOT NULL,
    city_id INTEGER NOT NULL REFERENCES cities(city_id),
    duration_hours INTEGER DEFAULT 2,
    image VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_routes_city ON routes(city_id);
CREATE INDEX idx_routes_mood ON routes(mood);

-- Безопасные зоны
CREATE TABLE safe_zones (
    zone_id SERIAL PRIMARY KEY,
    district VARCHAR(200) NOT NULL,
    city_id INTEGER NOT NULL REFERENCES cities(city_id),
    level VARCHAR(20) NOT NULL,
    note TEXT
);

CREATE INDEX idx_safezones_city ON safe_zones(city_id);

-- Сообщения "Потерялся" (обновлённая версия)
CREATE TABLE lost_reports (
    report_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id),
    lat FLOAT NOT NULL,
    lng FLOAT NOT NULL,
    message TEXT,
    status VARCHAR(20) DEFAULT 'pending' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lostreports_user ON lost_reports(user_id);
CREATE INDEX idx_lostreports_status ON lost_reports(status);