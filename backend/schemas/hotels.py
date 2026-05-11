# backend/app/schemas/hotels.py
from pydantic import BaseModel
from datetime import datetime
from typing import Optional, List


class HotelCreate(BaseModel):
    name: str
    city_id: int
    address: str
    description: Optional[str] = None
    price_per_night: int
    avg_city_price: int
    rating: float = 0.0
    capacity: int = 10
    manager_phones: Optional[List[str]] = None
    main_image: Optional[str] = None
    room_images: Optional[str] = None


class HotelUpdate(BaseModel):
    name: Optional[str] = None
    city_id: Optional[int] = None
    address: Optional[str] = None
    description: Optional[str] = None
    price_per_night: Optional[int] = None
    avg_city_price: Optional[int] = None
    rating: Optional[float] = None
    capacity: Optional[int] = None
    manager_phones: Optional[List[str]] = None
    main_image: Optional[str] = None
    room_images: Optional[str] = None
    status: Optional[int] = None


class HotelResponse(BaseModel):
    hotel_id: int
    name: str
    city_id: int
    city_name: str
    address: str
    description: Optional[str] = None
    price_per_night: int
    avg_city_price: int
    rating: float
    capacity: int = 10
    manager_phones: Optional[List[str]] = None
    main_image: Optional[str] = None
    room_images: Optional[str] = None
    status: int
    created_at: datetime

    class Config:
        from_attributes = True


class HotelListResponse(BaseModel):
    """Краткая информация об отеле для списка"""
    hotel_id: int
    name: str
    city_name: str
    price_per_night: int
    avg_city_price: int
    rating: float
    main_image: Optional[str] = None
    capacity: int  # добавим для информации

    class Config:
        from_attributes = True