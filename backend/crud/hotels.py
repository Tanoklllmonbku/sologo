from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, and_
from typing import List, Optional

from ..models import Hotel, City
from ..schemas.hotels import HotelCreate, HotelUpdate


async def get_all_hotels(
    db: AsyncSession, 
    city_id: Optional[int] = None,
    only_affordable: bool = False
) -> List[Hotel]:
    """Get all hotels with optional filters"""
    query = select(Hotel).where(Hotel.status == 1)
    
    if city_id:
        query = query.where(Hotel.city_id == city_id)
    
    if only_affordable:
        query = query.where(Hotel.price_per_night <= Hotel.avg_city_price)
    
    result = await db.execute(query)
    return result.scalars().all()


async def get_hotel_by_id(db: AsyncSession, hotel_id: int) -> Optional[Hotel]:
    """Get hotel by ID"""
    result = await db.execute(
        select(Hotel).where(Hotel.hotel_id == hotel_id)
    )
    return result.scalar_one_or_none()


async def create_hotel(db: AsyncSession, hotel_data: HotelCreate) -> Hotel:
    """Create new hotel (admin only)"""
    hotel = Hotel(**hotel_data.model_dump())
    db.add(hotel)
    await db.commit()
    await db.refresh(hotel)
    return hotel

# backend/crud/hotel.py
async def update_hotel(
    db: AsyncSession, 
    hotel_id: int, 
    hotel_data: HotelUpdate
) -> Optional[Hotel]:
    """Update hotel (admin only)"""
    # Используем прямую выборку без фильтра по status
    result = await db.execute(
        select(Hotel).where(Hotel.hotel_id == hotel_id)  # ← убрал filter по status
    )
    hotel = result.scalar_one_or_none()
    
    if not hotel:
        return None
    
    update_dict = hotel_data.model_dump(exclude_unset=True)
    for key, value in update_dict.items():
        setattr(hotel, key, value)
    
    await db.commit()
    await db.refresh(hotel)
    return hotel


async def delete_hotel(db: AsyncSession, hotel_id: int) -> bool:
    """Soft delete hotel (admin only)"""
    hotel = await get_hotel_by_id(db, hotel_id)
    if not hotel:
        return False
    hotel.status = 0  # soft delete
    await db.commit()
    return True


async def get_all_hotels_admin(db: AsyncSession) -> List[Hotel]:
    """Get all hotels including inactive (admin only)"""
    result = await db.execute(select(Hotel).order_by(Hotel.hotel_id))
    return result.scalars().all()


async def get_hotels_by_city(db: AsyncSession, city_id: int) -> List[Hotel]:
    """Get hotels by city ID"""
    result = await db.execute(
        select(Hotel).where(Hotel.city_id == city_id, Hotel.status == 1)
    )
    return result.scalars().all()

async def get_hotel_by_id_admin(db: AsyncSession, hotel_id: int) -> Optional[Hotel]:
    """Get hotel by ID without status filter (for admin)"""
    result = await db.execute(
        select(Hotel).where(Hotel.hotel_id == hotel_id)
    )
    return result.scalar_one_or_none()