# backend/app/routers/lost.py
from typing import List
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from core.database import get_db
from core.dependencies import get_current_user, require_admin
from adapter.lost_report import (
    create_lost_report, get_user_lost_reports, get_all_lost_reports, 
    update_lost_report_status
)
from adapter.user import get_user_by_id
from dto.lost_report import (
    LostReportCreate, LostReportResponse, LostReportStatusUpdate
)
from models import User

router = APIRouter(prefix="/lost", tags=["lost"])


@router.post("/report", response_model=LostReportResponse)
async def report_lost(
    report_data: LostReportCreate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Report that user is lost"""
    report = await create_lost_report(
        db=db,
        user_id=current_user.user_id,
        lat=report_data.lat,
        lng=report_data.lng,
        message=report_data.message or ""
    )
    
    return LostReportResponse(
        report_id=report.report_id,
        user_id=report.user_id,
        user_nickname=current_user.nickname,
        lat=report.lat,
        lng=report.lng,
        message=report.message,
        status=report.status,
        created_at=report.created_at
    )


@router.get("/my", response_model=List[LostReportResponse])
async def get_my_reports(
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Get all lost reports for current user"""
    reports = await get_user_lost_reports(db, current_user.user_id)
    
    result = []
    for report in reports:
        result.append(LostReportResponse(
            report_id=report.report_id,
            user_id=report.user_id,
            user_nickname=current_user.nickname,
            lat=report.lat,
            lng=report.lng,
            message=report.message,
            status=report.status,
            created_at=report.created_at
        ))
    return result


# ========== ADMIN ENDPOINTS ==========

@router.get("/admin/all", response_model=List[LostReportResponse])
async def get_all_lost_reports_endpoint(
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Get all lost reports (admin only)"""
    reports = await get_all_lost_reports(db)
    
    result = []
    for report in reports:
        user = await get_user_by_id(db, report.user_id)
        result.append(LostReportResponse(
            report_id=report.report_id,
            user_id=report.user_id,
            user_nickname=user.nickname if user else "",
            lat=report.lat,
            lng=report.lng,
            message=report.message,
            status=report.status,
            created_at=report.created_at
        ))
    return result


@router.patch("/admin/{report_id}/status", response_model=LostReportResponse)
async def update_report_status(
    report_id: int,
    status_data: LostReportStatusUpdate,
    admin: User = Depends(require_admin),
    db: AsyncSession = Depends(get_db)
):
    """Update lost report status (admin only)
    Statuses: pending, accepted, completed, cancelled
    """
    report = await update_lost_report_status(db, report_id, status_data.status)
    if not report:
        raise HTTPException(404, "Report not found")
    
    user = await get_user_by_id(db, report.user_id)
    return LostReportResponse(
        report_id=report.report_id,
        user_id=report.user_id,
        user_nickname=user.nickname if user else "",
        lat=report.lat,
        lng=report.lng,
        message=report.message,
        status=report.status,
        created_at=report.created_at
    )