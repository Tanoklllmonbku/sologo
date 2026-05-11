# backend/app/schemas/lost_report.py
from pydantic import BaseModel, field_validator
from datetime import datetime
from typing import Optional


class LostReportCreate(BaseModel):
    lat: float
    lng: float
    message: Optional[str] = None


class LostReportStatusUpdate(BaseModel):
    """Обновление статуса сообщения (админ)"""
    status: str  # pending, accepted, completed, cancelled

    @field_validator('status')
    @classmethod
    def validate_status(cls, v):
        allowed = ['pending', 'accepted', 'completed', 'cancelled']
        if v not in allowed:
            raise ValueError(f'status must be one of: {allowed}')
        return v


class LostReportResponse(BaseModel):
    report_id: int
    user_id: int
    user_nickname: str
    lat: float
    lng: float
    message: Optional[str] = None
    status: str  # pending, accepted, completed, cancelled
    created_at: datetime

    class Config:
        from_attributes = True