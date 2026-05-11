# backend/app/schemas/booking.py
from pydantic import BaseModel, field_validator
from datetime import datetime
from typing import Optional


class BookingCreate(BaseModel):
    hotel_id: int
    guests_count: int = 1
    check_in: datetime
    check_out: datetime

    @field_validator('check_in', 'check_out', mode='before')
    def remove_tz(cls, v):
        if hasattr(v, 'tzinfo') and v.tzinfo:
            return v.replace(tzinfo=None)
        return v


class BookingStatusUpdate(BaseModel):
    status: str


class BookingResponse(BaseModel):
    booking_id: int
    tracking_number: str
    user_id: int
    hotel_id: int
    hotel_name: str
    hotel_city: str
    guests_count: int
    check_in: datetime
    check_out: datetime
    total_price: int
    status: str
    created_at: datetime
    days: int

    class Config:
        from_attributes = True