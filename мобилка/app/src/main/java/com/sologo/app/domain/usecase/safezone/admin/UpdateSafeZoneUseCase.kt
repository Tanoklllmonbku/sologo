package com.sologo.app.domain.usecase.safezone.admin

import com.sologo.app.domain.model.SafeZone
import com.sologo.app.domain.repository.SafeZoneRepository
import com.sologo.app.utils.Result

class UpdateSafeZoneUseCase(
    private val safeZoneRepository: SafeZoneRepository
) {
    suspend operator fun invoke(
        zoneId: Int,
        district: String? = null,
        cityId: Int? = null,
        level: String? = null,
        note: String? = null
    ): Result<SafeZone> {
        if (zoneId <= 0) {
            return Result.Error("Неверный ID зоны")
        }
        if (level != null) {
            val validLevels = listOf("high", "medium", "low")
            if (level !in validLevels) {
                return Result.Error("Неверный уровень безопасности. Доступны: ${validLevels.joinToString()}")
            }
        }
        return safeZoneRepository.updateSafeZone(zoneId, district, cityId, level, note)
    }
}