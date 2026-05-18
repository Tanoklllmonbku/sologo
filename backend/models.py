# backend/models.py
from datetime import datetime
from sqlalchemy import ARRAY, Column, DateTime, ForeignKey, Integer, String, Float, Text, Boolean
from sqlalchemy.orm import declarative_base, relationship
import enum

Base = declarative_base()


# ENUM оставляем ТОЛЬКО для Python (не для БД)
class UserRole(str, enum.Enum):
    USER = "user"
    ADMIN = "admin"


class BookingStatus(str, enum.Enum):
    PENDING = "pending"
    CONFIRMED = "confirmed"
    CANCELLED = "cancelled"
    COMPLETED = "completed"


class User(Base):
    __tablename__ = "users"
    
    user_id = Column(Integer, primary_key=True, index=True)
    nickname = Column(String(100), nullable=False)
    email = Column(String(255), nullable=False, unique=True, index=True)
    passhash = Column(String(255), nullable=False)
    role = Column(String(20), default=UserRole.USER.value, nullable=False)  # VARCHAR
    phone_number = Column(String(20), nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    last_login = Column(DateTime, nullable=True)
    
    bookings = relationship("Booking", back_populates="user", cascade="all, delete-orphan")
    lost_reports = relationship("LostReport", back_populates="user")


class Booking(Base):
    __tablename__ = "bookings"
    
    booking_id = Column(Integer, primary_key=True, index=True)
    tracking_number = Column(String(50), unique=True, nullable=False, index=True)
    user_id = Column(Integer, ForeignKey("users.user_id"), nullable=False)
    hotel_id = Column(Integer, ForeignKey("hotels.hotel_id"), nullable=False)
    guests_count = Column(Integer, default=1)
    check_in = Column(DateTime, nullable=False)
    check_out = Column(DateTime, nullable=False)
    total_price = Column(Integer, nullable=False)
    status = Column(String(20), default=BookingStatus.PENDING.value)  # VARCHAR
    created_at = Column(DateTime, default=datetime.utcnow)
    cancelled_at = Column(DateTime, nullable=True)
    
    user = relationship("User", back_populates="bookings")
    hotel = relationship("Hotel", back_populates="bookings")


# Остальные модели без изменений...
class City(Base):
    __tablename__ = "cities"
    city_id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), nullable=False, unique=True)
    country = Column(String(100), nullable=False)
    hotels = relationship("Hotel", back_populates="city")
    routes = relationship("Route", back_populates="city")
    safe_zones = relationship("SafeZone", back_populates="city")
    created_at = Column(DateTime, default=datetime.utcnow)


class Hotel(Base):
    __tablename__ = "hotels"
    
    hotel_id = Column(Integer, primary_key=True, index=True)
    name = Column(String(200), nullable=False)
    city_id = Column(Integer, ForeignKey("cities.city_id", ondelete="CASCADE"), nullable=False)
    address = Column(String(300), nullable=False)
    description = Column(Text, nullable=True)
    price_per_night = Column(Integer, nullable=False)
    avg_city_price = Column(Integer, nullable=False)
    rating = Column(Float, default=0.0)
    capacity = Column(Integer, default=10)  # ← максимальное количество человек в отеле
    manager_phones = Column(ARRAY(String(20)), nullable=True)  # ← номер менеджера
    main_image = Column(String, nullable=True)
    room_images = Column(ARRAY(String), nullable=True)
    status = Column(Integer, default=1)
    created_at = Column(DateTime, default=datetime.utcnow)
    
    city = relationship("City", back_populates="hotels")
    bookings = relationship("Booking", back_populates="hotel")


class Route(Base):
    __tablename__ = "routes"
    route_id = Column(Integer, primary_key=True, index=True)
    title = Column(String(200), nullable=False)
    description = Column(Text, nullable=True)
    mood = Column(String(50), nullable=False)
    city_id = Column(Integer, ForeignKey("cities.city_id"), nullable=False)
    duration_hours = Column(Integer, default=2)
    image = Column(ARRAY(String), nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow) 
    
    city = relationship("City", back_populates="routes")


class SafeZone(Base):
    __tablename__ = "safe_zones"
    zone_id = Column(Integer, primary_key=True, index=True)
    district = Column(String(200), nullable=False)
    city_id = Column(Integer, ForeignKey("cities.city_id"), nullable=False)
    level = Column(String(20), nullable=False)
    note = Column(Text, nullable=True)
    city = relationship("City", back_populates="safe_zones")

# backend/app/models.py
import enum

class LostStatus(str, enum.Enum):
    PENDING = "pending"      # ожидает рассмотрения
    ACCEPTED = "accepted"    # принято в работу
    COMPLETED = "completed"  # решено/помогли
    CANCELLED = "cancelled"  # отменено (сам нашёлся)

class LostReport(Base):
    __tablename__ = "lost_reports"
    
    report_id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.user_id"), nullable=False)
    lat = Column(Float, nullable=False)
    lng = Column(Float, nullable=False)
    message = Column(Text, nullable=True)
    status = Column(String(20), default=LostStatus.PENDING.value)  # ← добавить статус
    created_at = Column(DateTime, default=datetime.utcnow)
    
    user = relationship("User", back_populates="lost_reports")