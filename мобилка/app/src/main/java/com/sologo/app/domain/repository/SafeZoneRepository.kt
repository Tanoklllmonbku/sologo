package com.sologo.app.domain.repository

import com.sologo.app.domain.model.SafeZone
import com.sologo.app.utils.Result

interface SafeZoneRepository {
    suspend fun getSafeZones(cityId: Int?): Result<List<SafeZone>>
    suspend fun createSafeZone(
        district: String,
        cityId: Int,
        level: String,
        note: String?
    ): Result<SafeZone>
    suspend fun updateSafeZone(
        zoneId: Int,
        district: String?,
        cityId: Int?,
        level: String?,
        note: String?
    ): Result<SafeZone>
    suspend fun deleteSafeZone(zoneId: Int): Result<Unit>
}