from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import List, Optional

from models import City
from dto.cities import CityCreate, CityUpdate


async def get_all_cities(db: AsyncSession) -> List[City]:
    """Get all cities"""
    result = await db.execute(select(City))
    return result.scalars().all()


async def get_city_by_id(db: AsyncSession, city_id: int) -> Optional[City]:
    """Get city by ID"""
    result = await db.execute(select(City).where(City.city_id == city_id))
    return result.scalar_one_or_none()


async def get_city_by_name(db: AsyncSession, name: str) -> Optional[City]:
    """Get city by name"""
    result = await db.execute(select(City).where(City.name == name))
    return result.scalar_one_or_none()


async def create_city(db: AsyncSession, city_data: CityCreate) -> City:
    """Create a new city"""
    city = City(
        name=city_data.name,
        country=city_data.country
    )
    db.add(city)
    await db.commit()
    await db.refresh(city)
    return city


async def update_city(
    db: AsyncSession, 
    city_id: int, 
    city_data: CityUpdate
) -> Optional[City]:
    """Update city by ID"""
    city = await get_city_by_id(db, city_id)
    if not city:
        return None

    if city_data.name is not None:
        city.name = city_data.name
    if city_data.country is not None:
        city.country = city_data.country

    await db.commit()
    await db.refresh(city)
    return city


async def delete_city(db: AsyncSession, city_id: int) -> bool:
    """Delete city by ID"""
    city = await get_city_by_id(db, city_id)
    if not city:
        return False
    await db.delete(city)
    await db.commit()
    return True
