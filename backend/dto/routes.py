# backend/app/schemas/route.py
from pydantic import BaseModel, field_validator
from datetime import datetime
from typing import Optional


class RouteCreate(BaseModel):
    title: str
    description: Optional[str] = None
    mood: str  # calm, active, cultural
    city_id: int
    duration_hours: int = 2
    image: Optional[str] = None

    @field_validator('mood')
    @classmethod
    def validate_mood(cls, v):
        allowed = ['calm', 'active', 'cultural']
        if v not in allowed:
            raise ValueError(f'mood must be one of: {allowed}')
        return v


class RouteUpdate(BaseModel):
    title: Optional[str] = None
    description: Optional[str] = None
    mood: Optional[str] = None
    city_id: Optional[int] = None
    duration_hours: Optional[int] = None
    image: Optional[str] = None

    @field_validator('mood')
    @classmethod
    def validate_mood(cls, v):
        if v is not None:
            allowed = ['calm', 'active', 'cultural']
            if v not in allowed:
                raise ValueError(f'mood must be one of: {allowed}')
        return v


class RouteResponse(BaseModel):
    route_id: int
    title: str
    description: Optional[str] = None
    mood: str
    city_id: int
    city_name: str
    duration_hours: int
    image: Optional[str] = None
    created_at: datetime

    class Config:
        from_attributes = True