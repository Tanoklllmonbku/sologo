# backend/app/crud/safe_zone.py
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import List, Optional

from models import SafeZone


async def get_all_safe_zones(db: AsyncSession) -> List[SafeZone]:
    """Get all safe zones"""
    result = await db.execute(select(SafeZone))
    return result.scalars().all()


async def get_safe_zones_by_city(db: AsyncSession, city_id: int) -> List[SafeZone]:
    """Get safe zones by city"""
    result = await db.execute(
        select(SafeZone).where(SafeZone.city_id == city_id)
    )
    return result.scalars().all()


async def create_safe_zone(db: AsyncSession, zone_data) -> SafeZone:
    """Create safe zone (admin only)"""
    zone = SafeZone(**zone_data.model_dump())
    db.add(zone)
    await db.commit()
    await db.refresh(zone)
    return zone


async def update_safe_zone(db: AsyncSession, zone_id: int, zone_data) -> Optional[SafeZone]:
    """Update safe zone (admin only)"""
    zone = await db.get(SafeZone, zone_id)
    if not zone:
        return None
    
    update_dict = zone_data.model_dump(exclude_unset=True)
    for key, value in update_dict.items():
        setattr(zone, key, value)
    
    await db.commit()
    await db.refresh(zone)
    return zone


async def delete_safe_zone(db: AsyncSession, zone_id: int) -> bool:
    """Delete safe zone (admin only)"""
    zone = await db.get(SafeZone, zone_id)
    if not zone:
        return False
    await db.delete(zone)
    await db.commit()
    return True