# backend/app/schemas/user.py
from pydantic import BaseModel, EmailStr
from datetime import datetime
from typing import Optional

from models import UserRole


class UserCreate(BaseModel):
    nickname: str
    email: EmailStr
    phone_number: Optional[str] = None
    password: str


class UserLogin(BaseModel):
    email: EmailStr
    password: str


class UserResponse(BaseModel):
    user_id: int
    nickname: str  # ← nickname, не name
    email: EmailStr
    phone_number: Optional[str] = None
    role: UserRole  # ← role, не role_id
    created_at: datetime

    class Config:
        from_attributes = True


class UserUpdate(BaseModel):
    nickname: Optional[str] = None
    email: Optional[EmailStr] = None
    phone_number: Optional[str] = None


class UserPasswordUpdate(BaseModel):
    old_password: str
    new_password: str