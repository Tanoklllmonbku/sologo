from typing import List
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from core.database import get_db
from core.dependencies import require_admin
from adapter.cities import (
    get_all_cities, get_city_by_id, create_city, update_city, delete_city
)
from dto.cities import CityCreate, CityUpdate, CityResponse
from models import User

router = APIRouter(prefix="/cities", tags=["cities"])


# ========== PUBLIC ENDPOINTS ==========

@router.get("/", response_model=List[CityResponse])
async def get_cities(db: AsyncSession = Depends(get_db)):
    """Get all cities (public)"""
    return await get_all_cities(db)


@router.get("/{city_id}", response_model=CityResponse)
async def get_city(
    city_id: int, 
    db: AsyncSession = Depends(get_db)
):
    """Get city by ID (public)"""
    city = await get_city_by_id(db, city_id)
    if not city:
        raise HTTPException(404, "City not found")
    return city


# ========== ADMIN ENDPOINTS ==========

@router.post("/", response_model=CityResponse)
async def create_city_endpoint(
    city_data: CityCreate,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Create new city (admin only)"""
    return await create_city(db, city_data)


@router.patch("/{city_id}", response_model=CityResponse)
async def update_city_endpoint(
    city_id: int,
    city_data: CityUpdate,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Update city by ID (admin only)"""
    city = await update_city(db, city_id, city_data)
    if not city:
        raise HTTPException(404, "City not found")
    return city


@router.delete("/{city_id}")
async def delete_city_endpoint(
    city_id: int,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Delete city by ID (admin only)"""
    success = await delete_city(db, city_id)
    if not success:
        raise HTTPException(404, "City not found")
    return {"message": "City deleted successfully"}
