from typing import List

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from ..core.security import create_access_token, verify_password, get_password_hash
from ..core.database import get_db
from ..core.dependencies import get_current_user, require_admin
from ..schemas.token import Token
from ..schemas.user import UserCreate, UserLogin, UserResponse, UserUpdate, UserPasswordUpdate
from ..models import User, UserRole
from ..crud.user import (
    create_user, get_all_users, get_user_by_phone, get_user_by_email, get_user_by_id, update_user
)

router = APIRouter(prefix="/api/v1", tags=["auth"])


# ========== USERS ENDPOINTS ==========

@router.get("/users/me", response_model=UserResponse)
async def get_me(
    current_user: User = Depends(get_current_user),
):
    """Get current authenticated user's profile"""
    return UserResponse(
        user_id=current_user.user_id,
        nickname=current_user.nickname,  # ← nickname, не name
        email=current_user.email,
        phone_number=current_user.phone_number,
        role=current_user.role,  # ← role, не role_id
        created_at=current_user.created_at
    )


@router.post("/register", response_model=UserResponse)
async def register(
    user_data: UserCreate, 
    db: AsyncSession = Depends(get_db)
):
    # Проверка на существующего пользователя
    if await get_user_by_email(db, user_data.email):
        raise HTTPException(400, "Email already registered")
    if user_data.phone_number and await get_user_by_phone(db, user_data.phone_number):
        raise HTTPException(400, "Phone number already registered")
    
    # Создаём пользователя с ролью USER по умолчанию
    new_user = await create_user(
        db=db,
        user_data=user_data,
        role=UserRole.USER  # ← используем enum
    )

    return UserResponse(
        user_id=new_user.user_id,
        nickname=new_user.nickname,
        email=new_user.email,
        phone_number=new_user.phone_number,
        role=new_user.role,
        created_at=new_user.created_at
    )


@router.post("/login", response_model=Token)  # ← убрал слэш в конце
async def login(
    user_data: UserLogin, 
    db: AsyncSession = Depends(get_db)
):
    user = await get_user_by_email(db, user_data.email)
    if not user:
        raise HTTPException(401, "Invalid email or password")
    
    if not verify_password(user_data.password, user.passhash):
        raise HTTPException(401, "Invalid password")

    access_token = create_access_token(data={"sub": str(user.user_id)})
    return Token(access_token=access_token, token_type="bearer")


@router.patch("/users/me", response_model=UserResponse)
async def update_me(
    updating_data: UserUpdate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Update own profile"""
    
    if updating_data.email and updating_data.email != current_user.email:
        if await get_user_by_email(db, updating_data.email):
            raise HTTPException(400, "Email already registered")
    
    if updating_data.phone_number and updating_data.phone_number != current_user.phone_number:
        if await get_user_by_phone(db, updating_data.phone_number):
            raise HTTPException(400, "Phone number already registered")
    
    updated_user = await update_user(db=db, user_id=current_user.user_id, user_data=updating_data)
    if not updated_user:
        raise HTTPException(404, "User not found")
    
    return updated_user


@router.patch("/users/me/password", response_model=dict)
async def update_password(
    password_data: UserPasswordUpdate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Update current user's password"""
    if not verify_password(password_data.old_password, current_user.passhash):
        raise HTTPException(400, "Incorrect old password")
    if verify_password(password_data.new_password, current_user.passhash):
        raise HTTPException(400, "New password must be different from old password")
    
    current_user.passhash = get_password_hash(password_data.new_password)
    await db.commit()
    
    return {"message": "Password updated successfully"}


@router.patch("/users/{user_id}", response_model=UserResponse)  
async def admin_update_user(  
    user_id: int, 
    user_data: UserUpdate,
    admin: User = Depends(require_admin),  # ← require_admin
    db: AsyncSession = Depends(get_db)
):
    """Update any user (admin only)"""
    
    current_target = await get_user_by_id(db, user_id)
    if not current_target:
        raise HTTPException(404, "User not found")
    
    if user_data.email and user_data.email != current_target.email:
        if await get_user_by_email(db, user_data.email):
            raise HTTPException(400, "Email already registered")
    
    if user_data.phone_number and user_data.phone_number != current_target.phone_number:
        if await get_user_by_phone(db, user_data.phone_number):
            raise HTTPException(400, "Phone number already registered")
    
    updated_user = await update_user(db=db, user_id=user_id, user_data=user_data)
    if not updated_user:
        raise HTTPException(404, "User not found")
    
    return updated_user


@router.get("/users/", response_model=List[UserResponse])
async def get_all_users_admin(
    admin: User = Depends(require_admin),  # ← require_admin
    db: AsyncSession = Depends(get_db)
):
    """Get all users (admin only)"""
    users = await get_all_users(db)
    if not users:
        raise HTTPException(404, "No users found")
    
    return [
        UserResponse(
            user_id=u.user_id,
            nickname=u.nickname,
            email=u.email,
            phone_number=u.phone_number,
            role=u.role,
            created_at=u.created_at
        )
        for u in users
    ]