# backend/app/routers/bookings.py
from typing import List
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload
from sqlalchemy import select
from datetime import datetime
import uuid

from core.database import get_db
from core.dependencies import get_current_user, require_admin
from adapter.bookings import (
    check_hotel_availability, create_booking, get_user_bookings, get_booking_by_tracking,
    cancel_booking, update_booking_status, get_all_bookings
)
from dto.bookings import BookingCreate, BookingResponse, BookingStatusUpdate
from models import User, Hotel

router = APIRouter(prefix="/bookings", tags=["bookings"])


# ========== USER BOOKINGS ==========

@router.post("/", response_model=BookingResponse)
async def create_booking_endpoint(
    booking_data: BookingCreate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Create a new booking for a hotel"""
    # Проверяем существование отеля с подгрузкой города
    result = await db.execute(
        select(Hotel)
        .where(Hotel.hotel_id == booking_data.hotel_id, Hotel.status == 1)
        .options(selectinload(Hotel.city))
    )
    hotel = result.scalar_one_or_none()
    
    if not hotel:
        raise HTTPException(404, "Hotel not found")
    
    # Проверяем, что check_in < check_out
    if booking_data.check_in >= booking_data.check_out:
        raise HTTPException(400, "Check-in date must be before check-out date")
    
    # Рассчитываем количество дней и общую стоимость
    days = (booking_data.check_out - booking_data.check_in).days
    if days <= 0:
        raise HTTPException(400, "Check-out date must be after check-in date")
    
    is_available, message, available_spots = await check_hotel_availability(
    db,
    booking_data.hotel_id,
    booking_data.check_in,
    booking_data.check_out,
    booking_data.guests_count
)

    if not is_available:
        raise HTTPException(400, detail=message)
    
    total_price = days * hotel.price_per_night * booking_data.guests_count
    
    # Генерируем tracking number
    tracking_number = f"TRK-{uuid.uuid4().hex[:8].upper()}-{datetime.utcnow().year}"
    
    try:
        # Убираем временную зону если есть
        check_in_naive = booking_data.check_in.replace(tzinfo=None) if booking_data.check_in.tzinfo else booking_data.check_in
        check_out_naive = booking_data.check_out.replace(tzinfo=None) if booking_data.check_out.tzinfo else booking_data.check_out
        
        booking = await create_booking(
            db=db,
            tracking_number=tracking_number,
            user_id=current_user.user_id,
            hotel_id=booking_data.hotel_id,
            guests_count=booking_data.guests_count,
            check_in=check_in_naive,
            check_out=check_out_naive,
            total_price=total_price
        )
    except ValueError as e:
        raise HTTPException(400, str(e))
    
    return BookingResponse(
        booking_id=booking.booking_id,
        tracking_number=booking.tracking_number,
        user_id=booking.user_id,
        hotel_id=booking.hotel_id,
        hotel_name=hotel.name,
        hotel_city=hotel.city.name if hotel.city else "",
        guests_count=booking.guests_count,
        check_in=booking.check_in,
        check_out=booking.check_out,
        total_price=booking.total_price,
        status=booking.status,
        created_at=booking.created_at,
        days=days
    )


@router.get("/my", response_model=List[BookingResponse])
async def get_my_bookings(
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Get all bookings for current user"""
    bookings = await get_user_bookings(db, current_user.user_id)
    result = []
    
    for booking in bookings:
        # Подгружаем отель с городом
        hotel = booking.hotel

        days = (booking.check_out - booking.check_in).days
        result.append(BookingResponse(
            booking_id=booking.booking_id,
            tracking_number=booking.tracking_number,
            user_id=booking.user_id,
            hotel_id=booking.hotel_id,
            hotel_name=hotel.name if hotel else "Unknown",
            hotel_city=hotel.city.name if hotel and hotel.city else "",
            guests_count=booking.guests_count,
            check_in=booking.check_in,
            check_out=booking.check_out,
            total_price=booking.total_price,
            status=booking.status,
            created_at=booking.created_at,
            days=days
        ))
    return result


@router.patch("/cancel/{tracking_number}", response_model=BookingResponse)
async def cancel_booking_endpoint(
    tracking_number: str,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Cancel booking by tracking number (owner only)"""
    booking = await cancel_booking(db, tracking_number, current_user.user_id)
    if not booking:
        raise HTTPException(404, "Booking not found or already cancelled")
    
    # Подгружаем отель с городом
    result = await db.execute(
        select(Hotel)
        .where(Hotel.hotel_id == booking.hotel_id)
        .options(selectinload(Hotel.city))
    )
    hotel = result.scalar_one_or_none()
    
    days = (booking.check_out - booking.check_in).days
    
    return BookingResponse(
        booking_id=booking.booking_id,
        tracking_number=booking.tracking_number,
        user_id=booking.user_id,
        hotel_id=booking.hotel_id,
        hotel_name=hotel.name if hotel else "Unknown",
        hotel_city=hotel.city.name if hotel and hotel.city else "",
        guests_count=booking.guests_count,
        check_in=booking.check_in,
        check_out=booking.check_out,
        total_price=booking.total_price,
        status=booking.status,
        created_at=booking.created_at,
        days=days
    )


# ========== ADMIN BOOKINGS ==========

@router.get("/admin/all", response_model=List[BookingResponse])
async def get_all_bookings_endpoint(
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Get all bookings (admin only)"""
    bookings = await get_all_bookings(db)
    result = []
    
    for booking in bookings:
        # Подгружаем отель с городом
        result_hotel = await db.execute(
            select(Hotel)
            .where(Hotel.hotel_id == booking.hotel_id)
            .options(selectinload(Hotel.city))
        )
        hotel = result_hotel.scalar_one_or_none()
        
        days = (booking.check_out - booking.check_in).days
        result.append(BookingResponse(
            booking_id=booking.booking_id,
            tracking_number=booking.tracking_number,
            user_id=booking.user_id,
            hotel_id=booking.hotel_id,
            hotel_name=hotel.name if hotel else "Unknown",
            hotel_city=hotel.city.name if hotel and hotel.city else "",
            guests_count=booking.guests_count,
            check_in=booking.check_in,
            check_out=booking.check_out,
            total_price=booking.total_price,
            status=booking.status,
            created_at=booking.created_at,
            days=days
        ))
    return result


@router.get("/admin/{tracking_number}", response_model=BookingResponse)
async def admin_get_booking(
    tracking_number: str,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Get booking by tracking number (admin only)"""
    booking = await get_booking_by_tracking(db, tracking_number)
    if not booking:
        raise HTTPException(404, "Booking not found")
    
    # Подгружаем отель с городом
    result = await db.execute(
        select(Hotel)
        .where(Hotel.hotel_id == booking.hotel_id)
        .options(selectinload(Hotel.city))
    )
    hotel = result.scalar_one_or_none()
    
    days = (booking.check_out - booking.check_in).days
    
    return BookingResponse(
        booking_id=booking.booking_id,
        tracking_number=booking.tracking_number,
        user_id=booking.user_id,
        hotel_id=booking.hotel_id,
        hotel_name=hotel.name if hotel else "Unknown",
        hotel_city=hotel.city.name if hotel and hotel.city else "",
        guests_count=booking.guests_count,
        check_in=booking.check_in,
        check_out=booking.check_out,
        total_price=booking.total_price,
        status=booking.status,
        created_at=booking.created_at,
        days=days
    )


@router.patch("/admin/{tracking_number}/status", response_model=BookingResponse)
async def admin_update_booking_status(
    tracking_number: str,
    status_data: BookingStatusUpdate,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Update booking status (admin only)"""
    booking = await update_booking_status(db, tracking_number, status_data.status)
    if not booking:
        raise HTTPException(404, "Booking not found")
    
    # Подгружаем отель с городом
    result = await db.execute(
        select(Hotel)
        .where(Hotel.hotel_id == booking.hotel_id)
        .options(selectinload(Hotel.city))
    )
    hotel = result.scalar_one_or_none()
    
    days = (booking.check_out - booking.check_in).days
    
    return BookingResponse(
        booking_id=booking.booking_id,
        tracking_number=booking.tracking_number,
        user_id=booking.user_id,
        hotel_id=booking.hotel_id,
        hotel_name=hotel.name if hotel else "Unknown",
        hotel_city=hotel.city.name if hotel and hotel.city else "",
        guests_count=booking.guests_count,
        check_in=booking.check_in,
        check_out=booking.check_out,
        total_price=booking.total_price,
        status=status_data.status,
        created_at=booking.created_at,
        days=days
    )