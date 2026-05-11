# backend/app/routers/safe_zones.py
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.ext.asyncio import AsyncSession

from ..core.database import get_db
from ..core.dependencies import require_admin
from ..crud.safe_zones import (
    get_all_safe_zones, get_safe_zones_by_city,
    create_safe_zone, update_safe_zone, delete_safe_zone
)
from ..crud.cities import get_city_by_id
from ..schemas.safe_zones import SafeZoneCreate, SafeZoneUpdate, SafeZoneResponse
from ..models import User

router = APIRouter(prefix="/safe-zones", tags=["safe-zones"])


@router.get("/", response_model=List[SafeZoneResponse])
async def get_safe_zones(
    city_id: Optional[int] = Query(None),
    db: AsyncSession = Depends(get_db)
):
    """Get safe zones, optionally filtered by city"""
    if city_id:
        zones = await get_safe_zones_by_city(db, city_id)
    else:
        zones = await get_all_safe_zones(db)
    
    result = []
    for zone in zones:
        city = await get_city_by_id(db, zone.city_id)
        result.append(SafeZoneResponse(
            zone_id=zone.zone_id,
            district=zone.district,
            city_id=zone.city_id,
            city_name=city.name if city else "",
            level=zone.level,
            note=zone.note
        ))
    return result


# ========== ADMIN ENDPOINTS ==========

@router.post("/", response_model=SafeZoneResponse)
async def create_safe_zone_endpoint(
    zone_data: SafeZoneCreate,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    city = await get_city_by_id(db, zone_data.city_id)
    if not city:
        raise HTTPException(400, "City not found")
    
    zone = await create_safe_zone(db, zone_data)
    return SafeZoneResponse(
        zone_id=zone.zone_id,
        district=zone.district,
        city_id=zone.city_id,
        city_name=city.name,
        level=zone.level,
        note=zone.note
    )


@router.patch("/{zone_id}", response_model=SafeZoneResponse)
async def update_safe_zone_endpoint(
    zone_id: int,
    zone_data: SafeZoneUpdate,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    zone = await update_safe_zone(db, zone_id, zone_data)
    if not zone:
        raise HTTPException(404, "Safe zone not found")
    
    city = await get_city_by_id(db, zone.city_id)
    return SafeZoneResponse(
        zone_id=zone.zone_id,
        district=zone.district,
        city_id=zone.city_id,
        city_name=city.name if city else "",
        level=zone.level,
        note=zone.note
    )


@router.delete("/{zone_id}")
async def delete_safe_zone_endpoint(
    zone_id: int,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    success = await delete_safe_zone(db, zone_id)
    if not success:
        raise HTTPException(404, "Safe zone not found")
    return {"message": "Safe zone deleted successfully"}