# backend/app/crud/user.py
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import Optional, List

from ..models import User, UserRole
from ..schemas.user import UserCreate, UserUpdate
from ..core.security import get_password_hash


async def create_user(
    db: AsyncSession, 
    user_data: UserCreate, 
    role: UserRole = UserRole.USER
) -> User:
    """Create user in DB"""
    new_user = User(
        nickname=user_data.nickname,
        email=user_data.email,
        phone_number=user_data.phone_number,
        passhash=get_password_hash(user_data.password),
        role=role  # ← enum, не role_id
    )
    db.add(new_user)
    await db.commit()
    await db.refresh(new_user)
    return new_user


async def update_user(
    db: AsyncSession, 
    user_id: int, 
    user_data: UserUpdate
) -> Optional[User]:
    """Update user data"""
    user = await get_user_by_id(db, user_id)
    if not user:
        return None
    
    update_dict = user_data.model_dump(exclude_unset=True)
    
    # Не даём менять роль через этот метод (только через админку отдельно)
    update_dict.pop("role", None)
    
    for key, value in update_dict.items():
        if hasattr(user, key):
            setattr(user, key, value)
    
    await db.commit()
    await db.refresh(user)
    return user


async def get_user_by_id(db: AsyncSession, user_id: int) -> Optional[User]:
    """Get user by ID"""
    result = await db.execute(
        select(User).where(User.user_id == user_id)
    )
    return result.scalar_one_or_none()


async def get_user_by_email(db: AsyncSession, email: str) -> Optional[User]:
    """Get user by email"""
    result = await db.execute(
        select(User).where(User.email == email)
    )
    return result.scalar_one_or_none()


async def get_user_by_nickname(db: AsyncSession, nickname: str) -> Optional[User]:
    """Get user by nickname"""
    result = await db.execute(
        select(User).where(User.nickname == nickname)
    )
    return result.scalar_one_or_none()


async def get_user_by_phone(db: AsyncSession, phone_number: str) -> Optional[User]:
    """Get user by phone number"""
    result = await db.execute(
        select(User).where(User.phone_number == phone_number)
    )
    return result.scalar_one_or_none()


async def get_all_users(db: AsyncSession) -> List[User]:
    """Get all users"""
    result = await db.execute(select(User))
    return result.scalars().all()


async def delete_user(db: AsyncSession, user_id: int) -> bool:
    """Delete user (soft delete or hard delete — пока hard)"""
    user = await get_user_by_id(db, user_id)
    if not user:
        return False
    await db.delete(user)
    await db.commit()
    return True


async def update_user_role(
    db: AsyncSession, 
    user_id: int, 
    new_role: UserRole
) -> Optional[User]:
    """Admin: change user role"""
    user = await get_user_by_id(db, user_id)
    if not user:
        return None
    user.role = new_role
    await db.commit()
    await db.refresh(user)
    return user
