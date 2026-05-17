package com.sologo.app.domain.usecase.lost

import com.sologo.app.domain.model.LostReport
import com.sologo.app.domain.repository.LostRepository
import com.sologo.app.utils.Result

class ReportLostUseCase(
    private val lostRepository: LostRepository
) {
    suspend operator fun invoke(
        lat: Double,
        lng: Double,
        message: String? = null
    ): Result<LostReport> {
        if (lat < -90 || lat > 90) {
            return Result.Error("Неверная широта")
        }
        if (lng < -180 || lng > 180) {
            return Result.Error("Неверная долгота")
        }
        return lostRepository.reportLost(lat, lng, message)
    }
}