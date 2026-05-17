# backend/app/crud/lost_report.py
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import List, Optional
from datetime import datetime

from models import LostReport, LostStatus


async def create_lost_report(
    db: AsyncSession,
    user_id: int,
    lat: float,
    lng: float,
    message: str
) -> LostReport:
    """Create lost report"""
    report = LostReport(
        user_id=user_id,
        lat=lat,
        lng=lng,
        message=message,
        status=LostStatus.PENDING.value
    )
    db.add(report)
    await db.commit()
    await db.refresh(report)
    return report


async def get_user_lost_reports(db: AsyncSession, user_id: int) -> List[LostReport]:
    """Get all lost reports for a user"""
    result = await db.execute(
        select(LostReport)
        .where(LostReport.user_id == user_id)
        .order_by(LostReport.created_at.desc())
    )
    return result.scalars().all()


async def get_all_lost_reports(db: AsyncSession) -> List[LostReport]:
    """Get all lost reports (admin only)"""
    result = await db.execute(
        select(LostReport).order_by(LostReport.created_at.desc())
    )
    return result.scalars().all()


async def get_lost_report_by_id(db: AsyncSession, report_id: int) -> Optional[LostReport]:
    """Get lost report by ID"""
    result = await db.execute(
        select(LostReport).where(LostReport.report_id == report_id)
    )
    return result.scalar_one_or_none()


async def update_lost_report_status(
    db: AsyncSession, 
    report_id: int, 
    new_status: str
) -> Optional[LostReport]:
    """Update lost report status (admin only)"""
    report = await get_lost_report_by_id(db, report_id)
    if not report:
        return None
    
    # Конвертируем строку в enum
    status_map = {
        "pending": LostStatus.PENDING,
        "accepted": LostStatus.ACCEPTED,
        "completed": LostStatus.COMPLETED,
        "cancelled": LostStatus.CANCELLED
    }
    
    if new_status not in status_map:
        return None
    
    report.status = status_map[new_status].value
    
    await db.commit()
    await db.refresh(report)
    return report