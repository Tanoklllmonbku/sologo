# backend/app/crud/route.py
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import List, Optional

from ..models import Route


async def get_all_routes(db: AsyncSession) -> List[Route]:
    """Get all routes"""
    result = await db.execute(select(Route).order_by(Route.route_id))
    return result.scalars().all()


async def get_route_by_id(db: AsyncSession, route_id: int) -> Optional[Route]:
    """Get route by ID"""
    result = await db.execute(select(Route).where(Route.route_id == route_id))
    return result.scalar_one_or_none()


async def get_routes_by_city(db: AsyncSession, city_id: int) -> List[Route]:
    """Get routes by city"""
    result = await db.execute(
        select(Route).where(Route.city_id == city_id)
    )
    return result.scalars().all()


async def get_routes_by_mood(db: AsyncSession, mood: str) -> List[Route]:
    """Get routes by mood (calm/active/cultural)"""
    result = await db.execute(
        select(Route).where(Route.mood == mood)
    )
    return result.scalars().all()


async def create_route(db: AsyncSession, route_data) -> Route:
    """Create new route (admin only)"""
    route = Route(**route_data.model_dump())
    db.add(route)
    await db.commit()
    await db.refresh(route)
    return route


async def update_route(db: AsyncSession, route_id: int, route_data) -> Optional[Route]:
    """Update route (admin only)"""
    route = await get_route_by_id(db, route_id)
    if not route:
        return None
    
    update_dict = route_data.model_dump(exclude_unset=True)
    for key, value in update_dict.items():
        setattr(route, key, value)
    
    await db.commit()
    await db.refresh(route)
    return route


async def delete_route(db: AsyncSession, route_id: int) -> bool:
    """Delete route (admin only)"""
    route = await get_route_by_id(db, route_id)
    if not route:
        return False
    await db.delete(route)
    await db.commit()
    return True