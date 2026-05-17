package com.sologo.app.domain.usecase.safezone

import com.sologo.app.domain.model.SafeZone
import com.sologo.app.domain.repository.SafeZoneRepository
import com.sologo.app.utils.Result

class GetSafeZonesUseCase(
    private val safeZoneRepository: SafeZoneRepository
) {
    suspend operator fun invoke(cityId: Int? = null): Result<List<SafeZone>> {
        return safeZoneRepository.getSafeZones(cityId)
    }
}