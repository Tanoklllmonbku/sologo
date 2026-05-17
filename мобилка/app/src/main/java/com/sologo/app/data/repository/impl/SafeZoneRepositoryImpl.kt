// data/repository/impl/SafeZoneRepositoryImpl.kt
package com.sologo.app.data.repository.impl

import com.sologo.app.data.mapper.SafeZoneMapper
import com.sologo.app.domain.model.SafeZone
import com.sologo.app.domain.repository.SafeZoneRepository
import com.sologo.app.models.request.safezone.SafeZoneCreateRequest
import com.sologo.app.models.request.safezone.SafeZoneUpdateRequest
import com.sologo.app.network.api.BookingApi
import com.sologo.app.network.api.SafeZoneApi
import com.sologo.app.utils.Result
import java.io.IOException

class SafeZoneRepositoryImpl(
    private val safeZoneApi: SafeZoneApi
) : SafeZoneRepository {

    override suspend fun getSafeZones(cityId: Int?): Result<List<SafeZone>> {
        return try {
            val response = safeZoneApi.getSafeZones(cityId)
            Result.Success(response.map { SafeZoneMapper.toDomain(it) })
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки безопасных зон: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun createSafeZone(
        district: String,
        cityId: Int,
        level: String,
        note: String?
    ): Result<SafeZone> {
        return try {
            val request = SafeZoneCreateRequest(district, cityId, level, note)
            val response = safeZoneApi.createSafeZone(request)
            Result.Success(SafeZoneMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка создания зоны: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun updateSafeZone(
        zoneId: Int,
        district: String?,
        cityId: Int?,
        level: String?,
        note: String?
    ): Result<SafeZone> {
        return try {
            val request = SafeZoneUpdateRequest(district, cityId, level, note)
            val response = safeZoneApi.updateSafeZone(zoneId, request)
            Result.Success(SafeZoneMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка обновления зоны: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun deleteSafeZone(zoneId: Int): Result<Unit> {
        return try {
            safeZoneApi.deleteSafeZone(zoneId)
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка удаления зоны: ${e.message ?: "Неизвестная ошибка"}")
        }
    }
}