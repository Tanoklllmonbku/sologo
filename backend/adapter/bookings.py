# backend/app/crud/booking.py
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from sqlalchemy.orm import selectinload
from typing import List, Optional, Tuple
from datetime import datetime

from models import Booking, BookingStatus, Hotel


async def create_booking(
    db: AsyncSession,
    tracking_number: str,
    user_id: int,
    hotel_id: int,
    guests_count: int,
    check_in: datetime,
    check_out: datetime,
    total_price: int
) -> Booking:
    """Create a new booking"""
    booking = Booking(
        tracking_number=tracking_number,
        user_id=user_id,
        hotel_id=hotel_id,
        guests_count=guests_count,
        check_in=check_in,
        check_out=check_out,
        total_price=total_price,
        status=BookingStatus.PENDING
    )
    
    db.add(booking)
    await db.commit()
    await db.refresh(booking)
    return booking


async def get_user_bookings(
    db: AsyncSession, 
    user_id: int
) -> List[Booking]:
    """Get all bookings for a specific user with hotel and city preloaded"""
    result = await db.execute(
        select(Booking)
        .where(Booking.user_id == user_id)
        .options(
            selectinload(Booking.hotel).selectinload(Hotel.city)  # ← подгружаем отель и город
        )
        .order_by(Booking.created_at.desc())
    )
    return result.scalars().all()


async def get_booking_by_id(
    db: AsyncSession, 
    booking_id: int
) -> Optional[Booking]:
    """Get booking by ID with hotel and city preloaded"""
    result = await db.execute(
        select(Booking)
        .where(Booking.booking_id == booking_id)
        .options(
            selectinload(Booking.hotel).selectinload(Hotel.city)
        )
    )
    return result.scalar_one_or_none()


async def get_booking_by_tracking(
    db: AsyncSession, 
    tracking_number: str
) -> Optional[Booking]:
    """Get booking by tracking number with hotel and city preloaded"""
    result = await db.execute(
        select(Booking)
        .where(Booking.tracking_number == tracking_number)
        .options(
            selectinload(Booking.hotel).selectinload(Hotel.city)
        )
    )
    return result.scalar_one_or_none()


async def cancel_booking(
    db: AsyncSession, 
    tracking_number: str, 
    user_id: int
) -> Optional[Booking]:
    """Cancel booking (user only)"""
    booking = await get_booking_by_tracking(db, tracking_number)
    if not booking or booking.user_id != user_id:
        return None
    
    if booking.status in [BookingStatus.CANCELLED, BookingStatus.COMPLETED]:
        return None
    
    booking.status = BookingStatus.CANCELLED
    booking.cancelled_at = datetime.utcnow()
    
    await db.commit()
    await db.refresh(booking)
    return booking


async def update_booking_status(
    db: AsyncSession, 
    tracking_number: str, 
    new_status: str
) -> Optional[Booking]:
    """Update booking status (admin only)"""
    booking = await get_booking_by_tracking(db, tracking_number)
    if not booking:
        return None
    
    # Конвертируем строку в enum
    status_map = {
        "pending": BookingStatus.PENDING,
        "confirmed": BookingStatus.CONFIRMED,
        "cancelled": BookingStatus.CANCELLED,
        "completed": BookingStatus.COMPLETED
    }
    
    if new_status not in status_map:
        return None
    
    booking.status = status_map[new_status]
    
    if new_status == "cancelled" and not booking.cancelled_at:
        booking.cancelled_at = datetime.utcnow()
    
    await db.commit()
    await db.refresh(booking)
    return booking


async def get_all_bookings(db: AsyncSession) -> List[Booking]:
    """Get all bookings with hotel and city preloaded (admin only)"""
    result = await db.execute(
        select(Booking)
        .options(
            selectinload(Booking.hotel).selectinload(Hotel.city)
        )
        .order_by(Booking.created_at.desc())
    )
    return result.scalars().all()


async def get_hotel_bookings(
    db: AsyncSession, 
    hotel_id: int
) -> List[Booking]:
    """Get all bookings for a specific hotel with city preloaded"""
    result = await db.execute(
        select(Booking)
        .where(Booking.hotel_id == hotel_id)
        .options(
            selectinload(Booking.hotel).selectinload(Hotel.city)
        )
        .order_by(Booking.check_in)
    )
    return result.scalars().all()

async def check_hotel_availability(
    db: AsyncSession,
    hotel_id: int,
    check_in: datetime,
    check_out: datetime,
    requested_guests: int
) -> Tuple[bool, str, int]:
    """Check if hotel has enough capacity"""
    # Получаем отель
    result = await db.execute(select(Hotel).where(Hotel.hotel_id == hotel_id))
    hotel = result.scalar_one_or_none()
    
    if not hotel:
        return False, "Отель не найден", 0
    
    # Находим все бронирования на эти даты
    result = await db.execute(
        select(Booking)
        .where(
            Booking.hotel_id == hotel_id,
            Booking.status.in_([BookingStatus.PENDING, BookingStatus.CONFIRMED]),
            Booking.check_in < check_out,
            Booking.check_out > check_in
        )
    )
    overlapping = result.scalars().all()
    
    # Считаем сколько гостей уже забронировано
    booked_guests = sum(b.guests_count for b in overlapping)
    
    # Свободные места
    available_spots = hotel.capacity - booked_guests
    
    if requested_guests > available_spots:
        if available_spots <= 0:
            return False, "❌ Мест нет", 0
        else:
            return False, f"⚠️ Осталось только {available_spots} мест", available_spots
    
    return True, f"✅ Доступно {available_spots} мест", available_spots