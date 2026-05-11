-- down_db.sql
\c postgres;

-- Отключаем внешние ключи
DROP TABLE IF EXISTS lost_reports CASCADE;
DROP TABLE IF EXISTS safe_zones CASCADE;
DROP TABLE IF EXISTS routes CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS hotels CASCADE;
DROP TABLE IF EXISTS cities CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Удаляем ENUM типы
DROP TYPE IF EXISTS booking_status CASCADE;
DROP TYPE IF EXISTS user_role CASCADE;

-- Подтверждение
\echo 'База данных sologo очищена. Таблицы удалены.'