package com.sologo.app.domain.usecase.safezone.admin

import com.sologo.app.domain.repository.SafeZoneRepository
import com.sologo.app.utils.Result

class DeleteSafeZoneUseCase(
    private val safeZoneRepository: SafeZoneRepository
) {
    suspend operator fun invoke(zoneId: Int): Result<Unit> {
        if (zoneId <= 0) {
            return Result.Error("Неверный ID зоны")
        }
        return safeZoneRepository.deleteSafeZone(zoneId)
    }
}