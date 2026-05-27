-- seed_db.sql

\c sologo;

-- ========== ГОРОДА ==========
INSERT INTO cities (name, country) VALUES
('Санкт-Петербург', 'Россия'),
('Москва', 'Россия'),
('Казань', 'Россия'),
('Сочи', 'Россия'),
('Калининград', 'Россия'),
('Нижний Новгород', 'Россия'),
('Екатеринбург', 'Россия'),
('Новосибирск', 'Россия');

-- ========== ПОЛЬЗОВАТЕЛИ (пароль = 'password123' хеширован) ==========
-- Хеш для 'password123' через bcrypt
INSERT INTO users (nickname, email, passhash, role, phone_number) VALUES
('Администратор', 'admin@sologo.ru', '$2b$12$XFSUOnkXvsuB.4/jHE5psecdCL93/f5BUpHjOT5BNiE7maBnLwluW', 'admin', '+79990000001'),
('Анна Петрова', 'anna@sologo.ru', '$2b$12$XFSUOnkXvsuB.4/jHE5psecdCL93/f5BUpHjOT5BNiE7maBnLwluW', 'user', '+79990000002'),
('Иван Сидоров', 'ivan@sologo.ru', '$2b$12$XFSUOnkXvsuB.4/jHE5psecdCL93/f5BUpHjOT5BNiE7maBnLwluW', 'user', '+79990000003'),
('Мария Иванова', 'maria@sologo.ru', '$2b$12$XFSUOnkXvsuB.4/jHE5psecdCL93/f5BUpHjOT5BNiE7maBnLwluW', 'user', '+79990000004');

-- ========== ОТЕЛИ ==========
-- Санкт-Петербург (city_id = 1)
INSERT INTO hotels (name, city_id, address, description, price_per_night, avg_city_price, rating, capacity, manager_phones, main_image, room_images, status) VALUES
('Отель "Северное небо"', 1, 'Невский пр-т, 88', 'Тихий дворик, завтрак, рядом метро', 4200, 5500, 4.5, 20, '["+7 (812) 123-45-67", "+7 (812) 234-56-78"]', '/static/main/1.png', ARRAY['/static/rooms/1_1.png', '/static/rooms/1_2.png', '/static/rooms/1_3.png'], 1),
('Хостел "Соло-лаунж"', 1, 'ул. Рубинштейна, 23', 'Капсулы и общая кухня', 1900, 5500, 4.2, 15, '["+7 (812) 345-67-89"]', '/static/main/2.png', ARRAY['/static/rooms/2_1.png', '/static/rooms/2_2.png', '/static/rooms/2_3.png'], 1),
('Отель "Эрмитаж"', 1, 'ул. Малая Морская, 14', 'Вид на Исаакиевский собор', 8500, 5500, 4.8, 30, '["+7 (812) 456-78-90", "+7 (812) 567-89-01"]', '/static/main/3.png', ARRAY['/static/rooms/3_1.png', '/static/rooms/3_2.png', '/static/rooms/3_3.png'], 1);

-- Москва (city_id = 2)
INSERT INTO hotels (name, city_id, address, description, price_per_night, avg_city_price, rating, capacity, manager_phones, main_image, room_images, status) VALUES
('Отель "Красная площадь"', 2, 'ул. Никольская, 10', 'В центре Москвы', 12000, 7000, 4.7, 25, '["+7 (495) 123-45-67"]', '/static/main/4.png', ARRAY['/static/rooms/4_1.png', '/static/rooms/4_2.png', '/static/rooms/4_3.png'], 1),
('Хостел "Центральный"', 2, 'Мясницкая ул., 15', 'Бюджетный вариант', 2500, 7000, 4.0, 40, '["+7 (495) 234-56-78", "+7 (495) 345-67-89"]', '/static/main/5.png', ARRAY['/static/rooms/5_1.png', '/static/rooms/5_2.png', '/static/rooms/5_3.png'], 1);

-- Казань (city_id = 3)
INSERT INTO hotels (name, city_id, address, description, price_per_night, avg_city_price, rating, capacity, manager_phones, main_image, room_images, status) VALUES
('Апарт "Казанская набережная"', 3, 'ул. Баумана, 45', 'Вид на воду, самостоятельное заселение', 3100, 4000, 4.4, 10, '["+7 (843) 123-45-67"]', '/static/main/6.png', ARRAY['/static/rooms/6_1.png', '/static/rooms/6_2.png', '/static/rooms/6_3.png'], 1);

-- Сочи (city_id = 4)
INSERT INTO hotels (name, city_id, address, description, price_per_night, avg_city_price, rating, capacity, manager_phones, main_image, room_images, status) VALUES
('Отель "Горный ветер"', 4, 'ул. Нагорная, 12', 'Премиум-зона и бассейн', 6800, 5200, 4.6, 18, '["+7 (862) 123-45-67", "+7 (862) 234-56-78"]', '/static/main/7.png', ARRAY['/static/rooms/7_1.png', '/static/rooms/7_2.png', '/static/rooms/7_3.png'], 1),
('Мини-отель "У моря"', 4, 'ул. Приморская, 5', 'Рядом с пляжем', 3500, 5200, 4.3, 12, '["+7 (862) 345-67-89"]', '/static/main/8.png', ARRAY['/static/rooms/8_1.png', '/static/rooms/8_2.png', '/static/rooms/8_3.png'], 1);

-- Калининград (city_id = 5)
INSERT INTO hotels (name, city_id, address, description, price_per_night, avg_city_price, rating, capacity, manager_phones, main_image, room_images, status) VALUES
('Мини-отель "Тихий двор"', 5, 'ул. Шевченко, 22', 'Семейный формат, завтраки', 2800, 4500, 4.5, 8, '["+7 (4012) 123-45-67"]', '/static/main/9.png', ARRAY['/static/rooms/9_1.png', '/static/rooms/9_2.png', '/static/rooms/9_3.png'], 1);

-- ========== МАРШРУТЫ ==========
INSERT INTO routes (title, description, mood, city_id, duration_hours, image) VALUES
('Утро у набережной', 'Кофе, прогулка вдоль воды, лёгкий завтрак', 'calm', 1, 3, '/static/routes/embankment.jpg'),
('Город на велосипеде', 'Аренда велика на полдня, объехать набережные', 'active', 4, 5, '/static/routes/bike.jpg'),
('Музейный день', 'Один главный музей + кофе, без плотного графика', 'cultural', 1, 4, '/static/routes/museum.jpg'),
('Парк и чтение', 'Тихий парк, скамейка с книгой или подкастом', 'calm', 3, 2, '/static/routes/park.jpg'),
('Пешеходный центр', 'Исторический квартал, аудиогид в наушниках', 'cultural', 5, 3, '/static/routes/walking.jpg'),
('Треккинг лёгкий', '8–12 км с перекусом, без экстрима', 'active', 4, 6, '/static/routes/trekking.jpg');

-- ========== БЕЗОПАСНЫЕ ЗОНЫ ==========
INSERT INTO safe_zones (district, city_id, level, note) VALUES
('Центр, Невский пр-т', 1, 'high', 'Людно вечером, следите за личными вещами'),
('Петроградская сторона', 1, 'high', 'Спокойный район, много кафе и парков'),
('Центр, Красная площадь', 2, 'high', 'Основные достопримечательности, патрули'),
('Казанский Кремль', 3, 'high', 'Туристы и патрули — ориентир для первого дня'),
('Центр', 4, 'medium', 'Вечером на набережной шумно, но безопасно'),
('Центр, Остров Канта', 5, 'high', 'Туристический центр, безопасно круглосуточно');

-- ========== ТЕСТОВЫЕ БРОНИРОВАНИЯ ==========
INSERT INTO bookings (tracking_number, user_id, hotel_id, guests_count, check_in, check_out, total_price, status) VALUES
('TRK-001-2024', 2, 1, 2, '2025-06-15 14:00:00', '2025-06-18 12:00:00', 12600, 'confirmed'),
('TRK-002-2024', 2, 4, 1, '2025-07-01 14:00:00', '2025-07-05 12:00:00', 48000, 'pending'),
('TRK-003-2024', 3, 2, 1, '2025-05-20 14:00:00', '2025-05-22 12:00:00', 3800, 'cancelled'),
('TRK-004-2024', 4, 6, 2, '2025-08-10 14:00:00', '2025-08-15 12:00:00', 17500, 'confirmed');

-- ========== СООБЩЕНИЯ "ПОТЕРЯЛСЯ" ==========
INSERT INTO lost_reports (user_id, lat, lng, message, status) VALUES
(2, 59.9343, 30.3351, 'Заблудился в Эрмитаже, нужна помощь', 'pending'),
(3, 55.7558, 37.6176, 'Потерял телефон на Красной площади', 'accepted'),
(4, 43.5855, 39.7231, 'Не могу найти отель, темно', 'completed'),
(2, 54.7348, 55.9579, 'Потерялся в парке, нужен гид', 'cancelled');