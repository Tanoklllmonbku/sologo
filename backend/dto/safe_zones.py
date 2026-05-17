# backend/app/schemas/safe_zone.py
from pydantic import BaseModel
from typing import Optional


class SafeZoneCreate(BaseModel):
    district: str
    city_id: int
    level: str  # high, medium, low
    note: Optional[str] = None


class SafeZoneUpdate(BaseModel):
    district: Optional[str] = None
    city_id: Optional[int] = None
    level: Optional[str] = None
    note: Optional[str] = None


class SafeZoneResponse(BaseModel):
    zone_id: int
    district: str
    city_id: int
    city_name: str
    level: str
    note: Optional[str] = None

    class Config:
        from_attributes = True