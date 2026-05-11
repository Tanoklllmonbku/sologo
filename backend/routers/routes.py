# backend/app/routers/routes.py
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.ext.asyncio import AsyncSession

from ..core.database import get_db
from ..core.dependencies import get_current_user, require_admin
from ..crud.routes import (
    get_all_routes, get_route_by_id, get_routes_by_city, get_routes_by_mood,
    create_route, update_route, delete_route
)
from ..crud.cities import get_city_by_id
from ..schemas.routes import RouteCreate, RouteUpdate, RouteResponse
from ..models import User

router = APIRouter(prefix="/routes", tags=["routes"])


# ========== PUBLIC ENDPOINTS ==========

@router.get("/", response_model=List[RouteResponse])
async def get_routes(
    city_id: Optional[int] = Query(None, description="Filter by city"),
    mood: Optional[str] = Query(None, description="Filter by mood (calm/active/cultural)"),
    db: AsyncSession = Depends(get_db)
):
    """Get all routes with filters"""
    if city_id:
        routes = await get_routes_by_city(db, city_id)
    elif mood:
        routes = await get_routes_by_mood(db, mood)
    else:
        routes = await get_all_routes(db)
    
    result = []
    for route in routes:
        city = await get_city_by_id(db, route.city_id)
        result.append(RouteResponse(
            route_id=route.route_id,
            title=route.title,
            description=route.description,
            mood=route.mood,
            city_id=route.city_id,
            city_name=city.name if city else "",
            duration_hours=route.duration_hours,
            image=route.image,
            created_at=route.created_at
        ))
    return result


@router.get("/{route_id}", response_model=RouteResponse)
async def get_route(
    route_id: int,
    db: AsyncSession = Depends(get_db)
):
    """Get route by ID"""
    route = await get_route_by_id(db, route_id)
    if not route:
        raise HTTPException(404, "Route not found")
    
    city = await get_city_by_id(db, route.city_id)
    return RouteResponse(
        route_id=route.route_id,
        title=route.title,
        description=route.description,
        mood=route.mood,
        city_id=route.city_id,
        city_name=city.name if city else "",
        duration_hours=route.duration_hours,
        image=route.image,
        created_at=route.created_at
    )


# ========== ADMIN ENDPOINTS ==========

@router.post("/", response_model=RouteResponse)
async def create_route_endpoint(
    route_data: RouteCreate,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Create new route (admin only)"""
    city = await get_city_by_id(db, route_data.city_id)
    if not city:
        raise HTTPException(400, "City not found")
    
    route = await create_route(db, route_data)
    return RouteResponse(
        route_id=route.route_id,
        title=route.title,
        description=route.description,
        mood=route.mood,
        city_id=route.city_id,
        city_name=city.name,
        duration_hours=route.duration_hours,
        image=route.image,
        created_at=route.created_at
    )


@router.patch("/{route_id}", response_model=RouteResponse)
async def update_route_endpoint(
    route_id: int,
    route_data: RouteUpdate,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Update route (admin only)"""
    route = await update_route(db, route_id, route_data)
    if not route:
        raise HTTPException(404, "Route not found")
    
    city = await get_city_by_id(db, route.city_id)
    return RouteResponse(
        route_id=route.route_id,
        title=route.title,
        description=route.description,
        mood=route.mood,
        city_id=route.city_id,
        city_name=city.name if city else "",
        duration_hours=route.duration_hours,
        image=route.image,
        created_at=route.created_at
    )


@router.delete("/{route_id}")
async def delete_route_endpoint(
    route_id: int,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Delete route (admin only)"""
    success = await delete_route(db, route_id)
    if not success:
        raise HTTPException(404, "Route not found")
    return {"message": "Route deleted successfully"}