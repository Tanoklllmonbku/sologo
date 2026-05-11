from pydantic import BaseModel
from datetime import datetime
from typing import Optional


class CityCreate(BaseModel):
    """Create city (admin only)"""
    name: str
    country: str


class CityUpdate(BaseModel):
    """Update city (admin only)"""
    name: Optional[str] = None
    country: Optional[str] = None


class CityResponse(BaseModel):
    """City response"""
    city_id: int
    name: str
    country: str
    created_at: datetime

    class Config:
        from_attributes = True
        