package com.sologo.app.domain.usecase.lost.admin

import com.sologo.app.domain.model.LostReport
import com.sologo.app.domain.repository.LostRepository
import com.sologo.app.utils.Result

class UpdateLostReportStatusUseCase(
    private val lostRepository: LostRepository
) {
    suspend operator fun invoke(reportId: Int, status: String): Result<LostReport> {
        if (reportId <= 0) {
            return Result.Error("Неверный ID отчёта")
        }
        val validStatuses = listOf("pending", "accepted", "completed", "cancelled")
        if (status !in validStatuses) {
            return Result.Error("Неверный статус. Доступны: ${validStatuses.joinToString()}")
        }
        return lostRepository.updateLostReportStatus(reportId, status)
    }
}