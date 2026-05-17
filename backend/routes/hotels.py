from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.ext.asyncio import AsyncSession

from core.database import get_db
from core.dependencies import require_admin
from adapter.hotels import (
    get_all_hotels, get_hotel_by_id, create_hotel, get_hotel_by_id_admin, update_hotel, delete_hotel
)
from adapter.cities import get_city_by_id
from dto.hotels import (
    HotelCreate, HotelUpdate, HotelResponse, HotelListResponse
)
from models import User

router = APIRouter(prefix="/hotels", tags=["hotels"])


# ========== PUBLIC ENDPOINTS ==========

@router.get("/", response_model=List[HotelListResponse])
async def list_hotels(
    city_id: Optional[int] = Query(None, description="Filter by city"),
    affordable: bool = Query(False, description="Only hotels ≤ city average"),
    db: AsyncSession = Depends(get_db)
):
    """Get list of hotels with filters"""
    hotels = await get_all_hotels(db, city_id=city_id, only_affordable=affordable)
    
    # Добавляем название города
    result = []
    for hotel in hotels:
        city = await get_city_by_id(db, hotel.city_id)
        result.append(HotelListResponse(
            hotel_id=hotel.hotel_id,
            name=hotel.name,
            city_name=city.name if city else "",
            price_per_night=hotel.price_per_night,
            avg_city_price=hotel.avg_city_price,
            rating=hotel.rating,
            main_image=hotel.main_image,
            capacity=hotel.capacity
        ))
    return result


@router.get("/{hotel_id}", response_model=HotelResponse)
async def get_hotel(
    hotel_id: int,
    db: AsyncSession = Depends(get_db)
):
    """Get hotel by ID"""
    hotel = await get_hotel_by_id(db, hotel_id)
    if not hotel:
        raise HTTPException(404, "Hotel not found")
    
    city = await get_city_by_id(db, hotel.city_id)
    
    return HotelResponse(
        hotel_id=hotel.hotel_id,
        name=hotel.name,
        city_id=hotel.city_id,
        city_name=city.name if city else "",
        address=hotel.address,
        description=hotel.description,
        price_per_night=hotel.price_per_night,
        avg_city_price=hotel.avg_city_price,
        rating=hotel.rating,
        main_image=hotel.main_image,
        room_images=hotel.room_images,
        status=hotel.status,
        created_at=hotel.created_at
    )


# ========== ADMIN ENDPOINTS ==========

@router.post("/", response_model=HotelResponse)
async def create_hotel_endpoint(
    hotel_data: HotelCreate,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Create new hotel (admin only)"""
    # Проверяем, что город существует
    city = await get_city_by_id(db, hotel_data.city_id)
    if not city:
        raise HTTPException(400, "City not found")
    
    hotel = await create_hotel(db, hotel_data)
    return await get_hotel(hotel.hotel_id, db)


@router.patch("/{hotel_id}", response_model=HotelResponse)
async def update_hotel_endpoint(
    hotel_id: int,
    hotel_data: HotelUpdate,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Update hotel (admin only)"""
    hotel = await update_hotel(db, hotel_id, hotel_data)
    if not hotel:
        raise HTTPException(404, "Hotel not found")
    return await get_hotel(hotel_id, db)


@router.delete("/{hotel_id}")
async def delete_hotel_endpoint(
    hotel_id: int,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Delete hotel (soft delete, admin only)"""
    success = await delete_hotel(db, hotel_id)
    if not success:
        raise HTTPException(404, "Hotel not found")
    return {"message": "Hotel deleted successfully"}


@router.patch("/admin/{hotel_id}/restore")
async def restore_hotel(
    hotel_id: int,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Restore soft-deleted hotel (admin only)"""
    hotel = await get_hotel_by_id_admin(db, hotel_id)  # нужна функция без фильтра
    if not hotel:
        raise HTTPException(404, "Hotel not found")
    hotel.status = 1
    await db.commit()
    return {"message": "Hotel restored successfully"}
