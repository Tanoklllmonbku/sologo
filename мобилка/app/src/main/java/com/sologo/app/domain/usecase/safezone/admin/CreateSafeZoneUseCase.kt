package com.sologo.app.domain.usecase.safezone.admin

import com.sologo.app.domain.model.SafeZone
import com.sologo.app.domain.repository.SafeZoneRepository
import com.sologo.app.utils.Result

class CreateSafeZoneUseCase(
    private val safeZoneRepository: SafeZoneRepository
) {
    suspend operator fun invoke(
        district: String,
        cityId: Int,
        level: String,
        note: String? = null
    ): Result<SafeZone> {
        if (district.isBlank()) {
            return Result.Error("Название района не может быть пустым")
        }
        if (cityId <= 0) {
            return Result.Error("Неверный ID города")
        }
        val validLevels = listOf("high", "medium", "low")
        if (level !in validLevels) {
            return Result.Error("Неверный уровень безопасности. Доступны: ${validLevels.joinToString()}")
        }
        return safeZoneRepository.createSafeZone(district, cityId, level, note)
    }
}